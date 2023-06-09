package com.melting.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.melting.domain.Crawling;

@Mapper
public interface CrawlingDAO {

	public void saveCrawlingData(Crawling crawling);

	public int countCrawlingData(String site);

	public void deleteOldData(String site);
	
	public List<Crawling> viewscntSortedData();

	public List<Crawling> getCrawlingList();

	public List<Crawling> getViewscntSortedList();

	public List<Crawling> getLikecntSortedList();
	
	public List<Crawling> getReplycntSortedList();

	public List<Crawling> getViewsSortedBestList();

	public List<Crawling> getViewsSortedHumorList();

	public List<Crawling> getViewsSortedGameList();

	public List<Crawling> getViewsSortedSportsList();

	public List<Crawling> search(String searchword);

	

	
	

}
