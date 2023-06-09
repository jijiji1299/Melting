package com.melting.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.melting.domain.Board;
import com.melting.domain.Member;

@Mapper
public interface BoardDAO {

	public int insertBoard(Board board);

	public Board read(int boardseq);

	public int delete(int boardseq);

	public int update(Board board);

	public int updateViewsCount(int boardseq);

	public List<Board> getRecommendList(int boardseq);

	public List<Board> getAllList();

	public void updateLikeCount(int boardseq);

	public List<Board> getWritedBestList();

	public List<Board> getWritedHumorList();

	public List<Board> getWritedGameList();

	public List<Board> getWritedSportsList();

	public List<Board> search(String searchword);
	
}
