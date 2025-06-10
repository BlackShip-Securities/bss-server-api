package com.bss.bssserverapi.domain.order.repository;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.order.dto.InMemoryOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisVirtualOrderBookRepository implements InMemoryVirtualOrderBookRepository{

    private final String PREFIX = "orderbook";
    private final String ASKS = "asks";
    private final String BIDS = "bids";
    private final RedissonClient redissonClient;

    @Override
    public Set<Long> findAccountIdsByCryptoId(final Long cryptoId) {

        return redissonClient.getKeys().getKeysStreamByPattern(PREFIX + ":" + cryptoId + ":*:*")
                .map(k -> Long.parseLong(k.split(":")[2]))
                .collect(Collectors.toSet());
    }

    @Override
    public NavigableSet<InMemoryOrderDto> findAsksByCryptoAndAccount(final Crypto crypto, final Account account) {

        return readSide(crypto.getId(), account.getId(), ASKS);
    }

    @Override
    public NavigableSet<InMemoryOrderDto> findBidsByCryptoAndAccount(final Crypto crypto, final Account account) {

        return readSide(crypto.getId(), account.getId(), BIDS);
    }

    @Override
    public void addAskByCryptoAndAccount(final Crypto crypto, final Account account, final InMemoryOrderDto order) {

        writeSide(crypto.getId(), account.getId(), ASKS, order);
    }

    @Override
    public void addBidByCryptoAndAccount(final Crypto crypto, final Account account, final InMemoryOrderDto order) {

        writeSide(crypto.getId(), account.getId(), BIDS, order);
    }

    @Override
    public void decrementAskQuantity(final Long cryptoId, final Long accountId, final Long orderId, final BigDecimal price, final BigDecimal executeQuantity) {

        decrementQuantity(cryptoId, accountId, ASKS, orderId, price, executeQuantity);
    }

    @Override
    public void decrementBidQuantity(final Long cryptoId, final Long accountId, final Long orderId, final BigDecimal price, final BigDecimal executeQuantity) {

        decrementQuantity(cryptoId, accountId, BIDS, orderId, price, executeQuantity);
    }

    /**
     * TODO: 동시성 제어
     * */
    private void decrementQuantity(final Long cryptoId, final Long accountId, final String side, final Long orderId, final BigDecimal price, final BigDecimal executeQuantity) {

        final RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(getKey(cryptoId, accountId, side));
        final double score = price.doubleValue();
        final Collection<String> collection = zSet.valueRange(score, true, score, true);
        final String prefix = orderId + ",";

        for(final String member : collection) {
            if(!member.startsWith(prefix))  continue;

            final InMemoryOrderDto dto = decode(member);
            BigDecimal remainingQuantity = dto.getQty().subtract(executeQuantity);

            zSet.remove(member);
            if(remainingQuantity.compareTo(BigDecimal.ZERO) > 0) {
                dto.subtractQty(executeQuantity);
                zSet.add(score, encode(dto));
            }
            break;
        }
    }

    private String getKey(final Long cryptoId, final Long accountId, final String side) {

        return String.format("%s:%d:%d:%s", PREFIX, cryptoId, accountId, side);
    }

    private NavigableSet<InMemoryOrderDto> readSide(final Long cryptoId, final Long accountId, final String side) {

        final RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(getKey(cryptoId, accountId, side));
        final Collection<String> members = side.equals(ASKS)
                ? zSet.valueRange(0, -1)
                : zSet.valueRangeReversed(0, -1);
        final Comparator<InMemoryOrderDto> cmp = side.equals(ASKS)
                ? Comparator.comparing(InMemoryOrderDto::getPrice)
                .thenComparing(InMemoryOrderDto::getOrderId)
                : Comparator.comparing(InMemoryOrderDto::getPrice).reversed()
                .thenComparing(InMemoryOrderDto::getOrderId);

        final NavigableSet<InMemoryOrderDto> set = new TreeSet<>(cmp);
        members.forEach(m -> set.add(this.decode(m)));

        return set;
    }

    private void writeSide(final Long cryptoId, final Long accountId, final String side, final InMemoryOrderDto dto) {

        redissonClient.getScoredSortedSet(getKey(cryptoId, accountId, side))
                .add(dto.getPrice().doubleValue(), encode(dto));
    }

    private String encode(final InMemoryOrderDto dto) {

        return dto.getOrderId() + "," + dto.getPrice() + "," + dto.getQty();
    }

    private InMemoryOrderDto decode(final String s) {

        final String[] p = s.split(",");
        return InMemoryOrderDto.builder()
                .orderId(Long.parseLong(p[0]))
                .price(new BigDecimal(p[1]))
                .qty(new BigDecimal(p[2]))
                .build();
    }
}
