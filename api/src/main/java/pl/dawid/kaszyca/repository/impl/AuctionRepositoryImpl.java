package pl.dawid.kaszyca.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.util.SloppyMath;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.config.SortEnum;
import pl.dawid.kaszyca.config.StateEnum;
import pl.dawid.kaszyca.dto.AttributeValuesDTO;
import pl.dawid.kaszyca.dto.CategoryAttributesDTO;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.model.auction.AuctionDetails;
import pl.dawid.kaszyca.vm.FilterVM;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class AuctionRepositoryImpl implements AuctionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private CriteriaBuilder cb;

    @Override
    public List findTop4ByCategoryOrderByViewers(String category) {
        cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = cb.createQuery();
        Root<Auction> auction = criteriaQuery.from(Auction.class);
        Predicate predicate = getCategoryPredicate(category, auction, cb);
        criteriaQuery.orderBy(cb.desc(auction.get("viewers")));
        criteriaQuery.select(auction).where(predicate);
        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
    }

    @Override
    public List findByFilters(FilterVM filterVM) {
        CriteriaQuery<Object> criteriaQuery = getCriteriaQuery(filterVM);
        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(filterVM.getPage() * filterVM.getPageSize())
                .setMaxResults(filterVM.getPageSize())
                .getResultList();
    }

    @Override
    public Long countByFilters(FilterVM filterVM) {
        cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<Auction> auction = criteriaQuery.from(Auction.class);
        Predicate predicate = getPredicateByFilters(filterVM, auction, cb);
        criteriaQuery.select(cb.count(auction)).where(predicate);
        if (filterVM.getFilters() != null && filterVM.getFilters().size() > 1) {
            addGroupBy(cb, criteriaQuery, filterVM, auction);
        }
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    private CriteriaQuery getCriteriaQuery(FilterVM filterVM) {
        cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root<Auction> auction = criteriaQuery.from(Auction.class);
        Predicate predicate = getPredicateByFilters(filterVM, auction, cb);
        criteriaQuery.select(auction).where(predicate);
        setSort(criteriaQuery, filterVM, cb, auction);
        if (filterVM.getFilters() != null && !filterVM.getFilters().isEmpty()) {
            addGroupBy(cb, criteriaQuery, filterVM, auction);
        }
        return criteriaQuery;
    }

    private Predicate getPredicateByFilters(FilterVM filterVM, Root<Auction> auction, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (filterVM.getSearchWords() != null && !filterVM.getSearchWords().isEmpty())
            predicates.add(getSearchPredicate(filterVM, cb, auction));
        if (!StringUtils.isEmpty(filterVM.getCondition())) {
            String fieldName = "condition";
            predicates.add(getEqualPredicate(fieldName, filterVM.getCondition(), auction, cb));
        }
        if (filterVM.isPriceFilter())
            predicates.add(getPricePredicate(filterVM, auction, cb));
        if (filterVM.getFilters() != null && !filterVM.getFilters().isEmpty())
            predicates.add(getAttributesPredicate(filterVM, auction, cb));
        if (!StringUtils.isEmpty(filterVM.getCategory()) && !filterVM.getCategory().equals("all"))
            predicates.add(getCategoryPredicate(filterVM.getCategory(), auction, cb));
        if (filterVM.getUserId() != null && filterVM.getUserId() != 0)
            predicates.add(getUserIdPredicate(filterVM.getUserId(), auction, cb));
        if (filterVM.getState() != StateEnum.ALL) {
            predicates.add(getState(cb, filterVM.getState(), auction));
        }
        if (filterVM.getCity() != null) {
            Expression<Auction> point1 = cb.function("point", Auction.class,
                    auction.get("city").get("longitude"), auction.get("city").get("latitude"));
            Expression<Double> point2 = cb.function("point", Double.class,
                    cb.literal(filterVM.getCity().getLongitude()), cb.literal(filterVM.getCity().getLatitude()));
            Expression<Number> distance = cb.function("ST_Distance_Sphere", Number.class,
                    point1, point2);
            int kilometersDiff = filterVM.getKilometers() * 1000;
            predicates.add(cb.le(distance, kilometersDiff));
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate getEqualPredicate(String fieldName, String condition, Root<Auction> auction, CriteriaBuilder cb) {
        return cb.equal(auction.get(fieldName), condition);
    }

    private Predicate getSearchPredicate(FilterVM filterVM, CriteriaBuilder cb, Root<Auction> auction) {
        List<Predicate> titleOrPredicates = new ArrayList<>();
        for (String word : filterVM.getSearchWords()) {
            titleOrPredicates.add(cb.or(cb.like(auction.get("title"), "%" + word + "%")));
        }
        return cb.or(titleOrPredicates.toArray(new Predicate[0]));
    }

    private Predicate getPricePredicate(FilterVM filterVM, Root<Auction> auction, CriteriaBuilder cb) {
        List<Predicate> pricePredicate = new ArrayList<>();
        if (filterVM.getMinPrice() != null) {
            pricePredicate.add(cb.greaterThanOrEqualTo(auction.get("price"), filterVM.getMinPrice()));
        }
        if (filterVM.getMaxPrice() != null) {
            pricePredicate.add(cb.lessThanOrEqualTo(auction.get("price"), filterVM.getMaxPrice()));
        }
        return cb.and(pricePredicate.toArray(new Predicate[0]));
    }

    private void setSort(CriteriaQuery criteriaQuery, FilterVM filterVM, CriteriaBuilder cb, Root<Auction> auction) {
        if (filterVM.getSort() != SortEnum.NONE) {
            criteriaQuery.orderBy(getOrder(filterVM, cb, auction));
        }
    }

    private Order getOrder(FilterVM filterVM, CriteriaBuilder cb, Root<Auction> auction) {
        if (filterVM.getSort() == SortEnum.ASC) {
            return cb.asc(auction.get(filterVM.getSortByFieldName()));
        } else {
            return cb.desc(auction.get(filterVM.getSortByFieldName()));
        }
    }

    private Predicate getAttributesPredicate(FilterVM filterVM, Root<Auction> auction, CriteriaBuilder cb) {
        List<Predicate> attributesPredicate = new ArrayList<>();
        Join<Auction, AuctionDetails> join = auction.join("auctionDetails", JoinType.LEFT);
        for (CategoryAttributesDTO obj : filterVM.getFilters()) {
            addAttributePredicate(cb, attributesPredicate, join, obj);
        }
        return cb.or(attributesPredicate.toArray(new Predicate[0]));
    }

    private void addAttributePredicate(CriteriaBuilder cb, List<Predicate> attributesPredicate, Join<Auction,
            AuctionDetails> join, CategoryAttributesDTO obj) {
        Predicate predicate = cb.equal(join.get("categoryAttribute"), obj.getAttribute());
        List<Predicate> att = new ArrayList<>();
        for (AttributeValuesDTO value : obj.getAttributeValues()) {
            att.add(cb.or(cb.equal(join.get("attributeValue"), value.getValue())));
        }
        Predicate attr = cb.or(att.toArray(new Predicate[0]));
        attributesPredicate.add(cb.and(attr, predicate));
    }

    private Predicate getCategoryPredicate(String category, Root<Auction> auction, CriteriaBuilder cb) {
        return cb.equal(auction.get("category").get("category"), category);
    }

    private Predicate getUserIdPredicate(Long userId, Root<Auction> auction, CriteriaBuilder cb) {
        return cb.equal(auction.get("user").get("id"), userId);
    }

    private Predicate getState(CriteriaBuilder cb, StateEnum state, Root<Auction> auction) {
        if (state == StateEnum.ACTIVE) {
            return cb.greaterThan(auction.<Instant>get("expiredDate"), new Date().toInstant());
        } else if (state == StateEnum.INACTIVE) {
            return cb.lessThanOrEqualTo(auction.<Instant>get("expiredDate"), new Date().toInstant());
        }
        return null;
    }

    private void addGroupBy(CriteriaBuilder cb, CriteriaQuery criteriaQuery, FilterVM filterVM, Root<Auction> auction) {
        criteriaQuery.groupBy(auction.get("id"));
        criteriaQuery.having(cb.greaterThanOrEqualTo(cb.count(auction.get("id")), Long.valueOf(filterVM.getFilters().size())));
    }
}
