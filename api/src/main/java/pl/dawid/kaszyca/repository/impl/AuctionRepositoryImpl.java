package pl.dawid.kaszyca.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import pl.dawid.kaszyca.config.SortEnum;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.model.auction.AuctionDetails;
import pl.dawid.kaszyca.vm.FilterVM;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class AuctionRepositoryImpl implements AuctionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public List findByFilters(FilterVM filterVM) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery();
        Root<Auction> auction = criteriaQuery.from(Auction.class);

        Predicate predicate = getPredicateByFilters(filterVM, auction, cb);
        criteriaQuery.select(auction).where(predicate);
        setSort(criteriaQuery, filterVM, cb, auction);
        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(filterVM.getPage())
                .setMaxResults(filterVM.getPageSize())
                .getResultList();
    }

    private Predicate getPredicateByFilters(FilterVM filterVM, Root<Auction> auction, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (filterVM.getSearchWords() != null && !filterVM.getSearchWords().isEmpty())
            predicates.add(getSearchPredicate(filterVM, cb, auction));
        if (!StringUtils.isEmpty(filterVM.getCondition())) {
            String fieldName = "condition";
            predicates.add(getEqualPredicate(fieldName, filterVM.getCondition(), auction, cb));
        }
        if (filterVM.isPriceFilter()) {
            predicates.add(getPricePredicate(filterVM, auction, cb));
        }
        if (filterVM.getFilters() != null && !filterVM.getFilters().isEmpty()) {
            predicates.add(getAttributesPredicate(filterVM, auction, cb));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private Predicate getEqualPredicate(String fieldName, String condition, Root<Auction> auction, CriteriaBuilder cb) {
        return cb.equal(auction.get(fieldName), condition);
    }

    private Predicate getSearchPredicate(FilterVM filterVM, CriteriaBuilder cb, Root<Auction> auction) {
        List<Predicate> titleOrPredicates = new ArrayList<Predicate>();
        for (String word : filterVM.getSearchWords()) {
            titleOrPredicates.add(cb.or(cb.like(auction.get("title"), "%" + word + "%")));
        }
        Predicate searchPredicate = cb.or(titleOrPredicates.toArray(new Predicate[titleOrPredicates.size()]));
        if (!StringUtils.isEmpty(filterVM.getCity())) {
            Predicate cityPredicate = cb.equal(auction.get("city").get("city"), filterVM.getCity());
            return cb.and(cityPredicate, searchPredicate);
        }
        return searchPredicate;
    }


    private Predicate getPricePredicate(FilterVM filterVM, Root<Auction> auction, CriteriaBuilder cb) {
        List<Predicate> pricePredicate = new ArrayList<>();
        if (filterVM.getMinPrice() != null) {
            pricePredicate.add(cb.greaterThanOrEqualTo(auction.get("price"), filterVM.getMinPrice()));
        }
        if (filterVM.getMaxPrice() != null) {
            pricePredicate.add(cb.lessThanOrEqualTo(auction.get("price"), filterVM.getMaxPrice()));
        }
        return cb.and(pricePredicate.toArray(new Predicate[pricePredicate.size()]));
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
        List<String> values;
        for (String key : filterVM.getFilters().keySet()) {
            values = filterVM.getFilters().get(key);
            Predicate predicate = cb.equal(join.get("categoryAttribute"), key);
            List<Predicate> att = new ArrayList<>();
            for (String value : values) {
                att.add(cb.or(cb.equal(join.get("attributeValue"), value)));
            }
            Predicate attr = cb.or(att.toArray(new Predicate[att.size()]));
            attributesPredicate.add(cb.and(attr, predicate));
        }
        return cb.and(attributesPredicate.toArray(new Predicate[attributesPredicate.size()]));
    }
}
