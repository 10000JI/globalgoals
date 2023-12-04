package dev.globalgoals.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPQLQuery;
import dev.globalgoals.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.security.Principal;
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
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable, String param, Principal principal) {
        log.info("searchPage.............................");

        QBoard board = QBoard.board;
        QBoardComment boardComment = QBoardComment.boardComment;
        QUser user = QUser.user;
        QBoardCategory category = QBoardCategory.boardCategory;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.leftJoin(user).on(board.user.eq(user));
        jpqlQuery.leftJoin(category).on(board.boardCategory.eq(category));
        jpqlQuery.leftJoin(boardComment).on(boardComment.board.eq(board));

        JPQLQuery<Tuple> tuple;

        if (param.equals("comment")) { //'내가 쓴 댓글' 이동 시 게시물 목록 대신 댓글 목록으로 변경
            tuple = jpqlQuery.select(board,boardComment,boardComment.count(),category);
        }
        else { // 그 외는 다음과 같이 통일
            //SELECT b, w, count(r), bcy FROM Board b
            //LEFT JOIN b.writer w LEFT JOIN b.boardCateroy bcy LEFT JOIN Reply r ON r.board = b
            tuple = jpqlQuery.select(board, user, boardComment.count(), category);
        }
        //where 조건
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (param.equals("free")) {
            booleanBuilder.and(board.boardCategory.id.eq(1L));
        } else if(param.equals("manner")){
            booleanBuilder.and(board.boardCategory.id.eq(2L));
        }else if(param.equals("fulfill")){
            booleanBuilder.and(board.boardCategory.id.eq(3L));
        } else {
            booleanBuilder.and(board.boardCategory.id.gt(0L)); //아래와 동문
        }

        booleanBuilder.and(board.id.gt(0L)); // SELECT 하여 객체 리스트를 볼때는 PK가 Auto_Increment라면 0부터 시작함을 적어줌

        if (param.equals("comment")) {
            booleanBuilder.and(boardComment.id.gt(0L)); // 이하 동문, '내가 쓴 댓글' 리스트
        }

        if (param.equals("mine")) {
            booleanBuilder.and(board.user.id.eq(principal.getName())); //게시글 작성자와 동일한 사용자라면 '내가 쓴 글' 리스트에 나타냄
        }

        if (param.equals("comment")) {
            booleanBuilder.and(boardComment.writer.eq(principal.getName())); //댓글 작성자와 동일한 사용자라면 '내가 쓴 댓글' 리스트에 나타냄
        }

        if(type != null){
            String[] typeArr = type.split("");
            //검색 조건을 작성하기
            BooleanBuilder conditionBuilder = new BooleanBuilder();

            for (String t:typeArr) {
                switch (t){
                    case "t": //제목
                        conditionBuilder.or(board.title.contains(keyword));
                        break;
                    case "w": //작성자
                        conditionBuilder.or(user.id.contains(keyword));
                        break;
                    case "c": //내용
                        conditionBuilder.or(board.content.contains(keyword));
                        break;
                    case "l": //카테고리
                        conditionBuilder.or(replaceCategoryName(category.categoryName).contains(keyword));
                        break;
                }
            }
            booleanBuilder.and(conditionBuilder);
        }

        tuple.where(booleanBuilder);

        //order by
        Sort sort = pageable.getSort(); //pageable은 파라미터로 받아 정렬 정보를 가져옴

        //tuple.orderBy(board.bno.desc()); //예시: 직접 코드로 처리하면

        sort.stream().forEach(order -> { //정렬 정보를 순회
            Order direction = order.isAscending()? Order.ASC: Order.DESC; //현재 정렬 항목이 오름차순인지 내림차순인지에 따라 Order 객체 생성 (isAscending(): 현재 정렬이 오름차순인지 여부 반환)
            String prop = order.getProperty(); //현재 정렬 항목의 속성(ex>필드명)을 가져온다. 각 정렬 항목은 bno, title 등을 가지고 있어 쿼리 정렬 기준으로 사용

            if (param.equals("comment")) {
                PathBuilder orderByExpression = new PathBuilder(BoardComment.class, "boardComment");
                tuple.orderBy(new OrderSpecifier(direction, orderByExpression.get(prop)));
            }else{
                PathBuilder orderByExpression = new PathBuilder(Board.class, "board"); //Querydsl에서 사용하는 PathBuilder를 생성한다. 여기서는 Board 엔티티를 기준으로 한다.
                tuple.orderBy(new OrderSpecifier(direction, orderByExpression.get(prop))); //Querydsl의 JPQLQuery<Tuple>에 정렬 정보를 추가
            }

        });


        if (param.equals("comment")) { //동일한 댓글 작성자가 동일한 게시글에 여러 댓글을 작성한 경우에도 모든 댓글을 가져오고 싶다면 GROUP BY 문에서 comment_num을 고려하여 그룹화를 수행
            tuple.groupBy(board,boardComment);
        } else { //댓글 리스트를 불러오지 않는 경우에는 Board의 id만 그룹화
            tuple.groupBy(board);
        }

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

    // categoryName을 대체하는 메서드 추가 - 카테고리 검색 (categoryName이 free일 땐 "자유 게시판" / manner일 땐 "실천 방법 등록" / fulfill일 땐 "실천 등록")
    private StringExpression replaceCategoryName(StringExpression categoryName) {
        return categoryName
                .when("free").then("자유 게시판")
                .when("manner").then("실천 방법 등록")
                .when("fulfill").then("실천 등록")
                .otherwise(categoryName);
    }
}
