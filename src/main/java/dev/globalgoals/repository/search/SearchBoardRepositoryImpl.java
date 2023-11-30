package dev.globalgoals.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import dev.globalgoals.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport implements SearchBoardRepository {

    public SearchBoardRepositoryImpl() {
        super(Board.class);
    }
    @Override
    public Board search1() {
        log.info("search1........................");

        QBoard board = QBoard.board;
        QBoardComment boardComment = QBoardComment.boardComment;
        QUser user = QUser.user;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.leftJoin(user).on(board.user.eq(user));
        jpqlQuery.leftJoin(boardComment).on(boardComment.board.eq(board));

        JPQLQuery<Tuple> tuple = jpqlQuery.select(board, user.email, boardComment.count());
        tuple.groupBy(board);

        log.info("---------------------------");
        log.info("tuple: {}",tuple);
        log.info("---------------------------");

        List<Tuple> result = tuple.fetch();

        log.info("result: {}", result);

        return null;
    }

    @Override
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable, String param) {
        log.info("searchPage.............................");

        QBoard board = QBoard.board;
        QBoardComment boardComment = QBoardComment.boardComment;
        QUser user = QUser.user;
        QBoardCategory category = QBoardCategory.boardCategory;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.leftJoin(user).on(board.user.eq(user));
        jpqlQuery.leftJoin(category).on(board.boardCategory.eq(category));
        jpqlQuery.leftJoin(boardComment).on(boardComment.board.eq(board));

        //SELECT b, w, count(r), bcy FROM Board b
        //LEFT JOIN b.writer w LEFT JOIN b.boardCateroy bcy LEFT JOIN Reply r ON r.board = b
        JPQLQuery<Tuple> tuple = jpqlQuery.select(board, user, boardComment.count(), category);

        //where 조건
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (param.equals("free")) {
            booleanBuilder.and(board.boardCategory.id.eq(1L));
        } else if(param.equals("manner")){
            booleanBuilder.and(board.boardCategory.id.eq(2L));
        }else if(param.equals("fulfill")){
            booleanBuilder.and(board.boardCategory.id.eq(3L));
        }
        BooleanExpression expression = board.id.gt(0L);
        booleanBuilder.and(expression);

        if(type != null){
            String[] typeArr = type.split("");
            //검색 조건을 작성하기
            BooleanBuilder conditionBuilder = new BooleanBuilder();

            for (String t:typeArr) {
                switch (t){
                    case "t":
                        conditionBuilder.or(board.title.contains(keyword));
                        break;
                    case "w":
                        conditionBuilder.or(user.id.contains(keyword));
                        break;
                    case "c":
                        conditionBuilder.or(board.content.contains(keyword));
                        break;
                }
            }
            booleanBuilder.and(conditionBuilder);
        }

        tuple.where(booleanBuilder);

        //order by
        Sort sort = pageable.getSort();

        //tuple.orderBy(board.bno.desc());

        sort.stream().forEach(order -> {
            Order direction = order.isAscending()? Order.ASC: Order.DESC;
            String prop = order.getProperty();

            PathBuilder orderByExpression = new PathBuilder(Board.class, "board");
            tuple.orderBy(new OrderSpecifier(direction, orderByExpression.get(prop)));

        });
        tuple.groupBy(board);

        //page 처리
        tuple.offset(pageable.getOffset());
        tuple.limit(pageable.getPageSize());

        List<Tuple> result = tuple.fetch();

        log.info("result:" +result);

        long count = tuple.fetchCount();

        log.info("COUNT: " +count);

        return new PageImpl<Object[]>(
                result.stream().map(t -> t.toArray()).collect(Collectors.toList()),
                pageable,
                count);
    }
}
