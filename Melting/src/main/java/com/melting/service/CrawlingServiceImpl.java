package com.melting.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.melting.dao.CrawlingDAO;
import com.melting.domain.Crawling;

@Service
public class CrawlingServiceImpl implements CrawlingService {
	
	@Autowired
	CrawlingDAO crawlingDao;
		
	public List<Crawling> getDcInsideCrawlingData() {
        List<Crawling> crawlingDataList = new ArrayList<>();

        try {
            // 디시인사이드 실시간 베스트 URL
            String dcUrl = "https://www.dcinside.com/";
            Document document = Jsoup.connect(dcUrl).get();

            Elements titles = document.select("div.box.besttxt > p");
            Elements replycnts = document.select("div.box.besttxt > span");
            Elements kinds = document.select("div.box.best_info > span.name");
            Elements links = document.select("div.time_best .main_log");
            
            int count = Math.min(10, titles.size()); // 10개 이하의 게시물만 가져오기 위해 크기 제한
            for (int i = 0; i < count; i++) {
                Element titleElement = titles.get(i);
                String title = titleElement.text();

                Element replycntElement = replycnts.get(i);
                String replycnt = replycntElement.text().replace("[", "").replace("]", "");
                
                Element kindElement = kinds.get(i);
                String kind = kindElement.text();
                
                Element linkElement = links.get(i);
                String link = linkElement.attr("href");
                
                // 게시물 페이지로 접속
                Document postDocument = Jsoup.connect(link).get();
                
                Element membernameElement = postDocument.selectFirst(".nickname");
                String membername;
                
                if (membernameElement == null) {
                	membername = "작성자 비공개";
                } else {
                	membername = membernameElement.text();
                }
                
                Element likecntElement = postDocument.selectFirst(".gall_reply_num");
                String likecnt = likecntElement.text().replace("추천", "").trim();
                
                // likecnt 값을 int로 변환
                int likecnt2 = Integer.parseInt(likecnt);
                
                Crawling crawling = Crawling.builder()
                        .title(title)
                        .replycnt(replycnt)
                        .kind(kind)
                        .link(link)
                        .membername(membername)
                        .likecnt2(likecnt2)
                        .build();

                crawlingDataList.add(crawling);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return crawlingDataList;
    }
	
	public List<Crawling> getFmKoreaCrawlingData() {
        List<Crawling> crawlingDataList = new ArrayList<>();

        try {
            String fmKoreaUrl = "https://www.fmkorea.com/index.php?mid=best2&listStyle=list&page=1";
            Document document = Jsoup.connect(fmKoreaUrl).get();

            Elements titles = document.select(".hx");
            Elements replycnts = document.select(".replyNum");
            Elements kinds = document.select(".bd_lst.bd_tb_lst.bd_tb tr td:nth-child(1) a");
            Elements links = document.select(".hx");
            Elements membernames = document.select(".author");
            Elements likecnts = document.select(".m_no_voted");
            

            int count = Math.min(10, replycnts.size());
            for (int i = 0; i < count; i++) {
                Element titleElement = titles.get(i);
                String title = titleElement.ownText();

                Element replycntElement = replycnts.get(i);
                String replycnt = replycntElement.text();
                
                Element kindElement = kinds.get(i);
                String kind = kindElement.text();
                
                Element linkElement = links.get(i);
                String link = "https://www.fmkorea.com"+linkElement.attr("href");
                
                Element membernameElement = membernames.get(i);
                String membername = membernameElement.text();
                
                Element likecntElement = likecnts.get(i);
                String likecnt = likecntElement.text().trim();
                

                
             // likecnt 값을 int로 변환
                int likecnt2 = Integer.parseInt(likecnt);
                
                Crawling crawling = Crawling.builder()
                        .title(title)
                        .replycnt(replycnt)
                        .kind(kind)
                        .link(link)
                        .membername(membername)
                        .likecnt2(likecnt2)
                        .build();
                
                crawlingDataList.add(crawling);
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return crawlingDataList;
    }

    
    public List<Crawling> getPpomppuCrawlingData() {
        List<Crawling> crawlingDataList = new ArrayList<>();

        try {
            String ppomppuUrl = "https://www.ppomppu.co.kr/hot.php?category=2";
            Document document = Jsoup.connect(ppomppuUrl).get();

            Elements titles = document.select(".line .title");
            Elements replycnts = document.select(".list_comment2");
            Elements kinds = document.select(".board_left a");
            Elements links = document.select(".line .title");	
            Elements membernames = document.select(".name");
            Elements likecnts = document.select("table.board_table tr.line td:nth-child(6)");

            int count = Math.min(10, titles.size());
            for (int i = 0; i < count; i++) {
                Element titleElement = titles.get(i);
                String title = titleElement.text();

                Element replycntElement = replycnts.get(i);
                String replycnt = replycntElement.text();
                
                Element kindElement = kinds.get(i+1);
                String kind = kindElement.text();
                
                Element linkElement = links.get(i);
                String link = "https://www.ppomppu.co.kr"+linkElement.attr("href");
                
                Element membernameElement = membernames.get(i);
                String membername = membernameElement.text();
                
                Element likecntElement = likecnts.get(i);
                String likecnt = likecntElement.text();
                
                int hyphenIndex = likecnt.indexOf("-");
                if (hyphenIndex != -1) {
                    likecnt = likecnt.substring(0, hyphenIndex).trim();
                }
                

             // likecnt 값을 int로 변환
                int likecnt2 = Integer.parseInt(likecnt);
                
                Crawling crawling = Crawling.builder()
                        .title(title)
                        .replycnt(replycnt)
                        .kind(kind)
                        .link(link)
                        .membername(membername)
                        .likecnt2(likecnt2)
                        .build();
                
                crawlingDataList.add(crawling);
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return crawlingDataList;
    }

//	public boolean saveCrawlingData(Crawling crawling) {
//		return crawlingDao.saveCrawlingData(crawling);
//		
//	}

	@Override
	public List<Crawling> getDcSearchCrawlingData() {
		List<Crawling> crawlingDataList = new ArrayList<>();
		
		try {
			
			String dcUrl = "https://www.dcinside.com/";
            Document document = Jsoup.connect(dcUrl).get();

            Elements titles = document.select(".rank_txt");
            Elements links = document.select(".busygall");
            
            int count = Math.min(10, titles.size());
            for (int i = 0; i < count; i++) {
                Element titleElement = titles.get(i);
                String title = titleElement.text();

                Element linkElement = links.get(i);
                String link = linkElement.attr("href");

                Crawling crawling = Crawling.builder()
                        .title(title)
                        .link(link)
                        .build();

                crawlingDataList.add(crawling);
                
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return crawlingDataList;
	}

	@Override
	public List<Crawling> getHitCrawlingData() {
		List<Crawling> crawlingDataList = new ArrayList<>();
		
		try {
			
			String dcUrl = "https://www.dcinside.com/";
            Document document = Jsoup.connect(dcUrl).get();

            Elements titles = document.select(".txt_box .tit");
            Elements images = document.select(".img_box img");
            Elements links = document.select(".link_thumb.main_log");
            
            int count = Math.min(4, images.size());
            for (int i = 0; i < count; i++) {
            	
            	Element titleElement = titles.get(i);
                String title = titleElement.text();
            	
                Element imageElement = images.get(i);
                String image = imageElement.attr("src");
                
                Element linkElement = links.get(i);
                String link = linkElement.attr("href");

                Crawling crawling = Crawling.builder()
                		.title(title)
                        .image(image)
                        .link(link)
                        .build();

                crawlingDataList.add(crawling);
                
            }
            
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return crawlingDataList;
	}
	
	
	public List<Crawling> getLikecntSortedData() {
	    List<Crawling> combinedDataList = new ArrayList<>();

	    // 디시인사이드 데이터 가져오기
	    List<Crawling> dcInsideDataList = getDcInsideCrawlingData();
	    combinedDataList.addAll(dcInsideDataList);

	    // FM코리아 데이터 가져오기
	    List<Crawling> fmKoreaDataList = getFmKoreaCrawlingData();
	    combinedDataList.addAll(fmKoreaDataList);

	    // 뽐뿌 데이터 가져오기
	    List<Crawling> ppomppuDataList = getPpomppuCrawlingData();
	    combinedDataList.addAll(ppomppuDataList);

	    // likecnt를 기준으로 내림차순 정렬
	    Collections.sort(combinedDataList, Comparator.comparing(Crawling::getLikecnt2).reversed());

	    return combinedDataList;
	}

	

	
//	// DB에 실시간 크롤링 데이터 저장
//    public void saveCrawlingData() {
//        List<Crawling> dcInsideCrawlingDataList = getDcInsideCrawlingData();
//        List<Crawling> fmKoreaCrawlingDataList = getFmKoreaCrawlingData();
//        List<Crawling> ppomppuDataList = getPpomppuCrawlingData();
//
//        for (Crawling crawling : dcInsideCrawlingDataList) {
//            crawlingDao.saveCrawlingData(crawling); //DB에 저장
//        }
//        for (Crawling crawling : fmKoreaCrawlingDataList) {
//            crawlingDao.saveCrawlingData(crawling); //DB에 저장
//        }
//        for (Crawling crawling : ppomppuDataList) {
//            crawlingDao.saveCrawlingData(crawling); //DB에 저장
//        }
//    }

	
	
//	// 마다 실행되도록 스케줄링 설정
//    @Scheduled(fixedRate = 10000)
//    public void saveCrawlingData() {
//        List<Crawling> dcInsideCrawlingDataList = getDcInsideCrawlingData();
////        List<Crawling> fmKoreaCrawlingDataList = getFmKoreaCrawlingData();
////        List<Crawling> ppomppuCrawlingDataList = getPpomppuCrawlingData();
//
//        for (Crawling crawling : dcInsideCrawlingDataList) {
//            crawlingDao.saveCrawlingData(crawling); // 디시인사이드 데이터를 DB에 저장
//        }
//
////        for (Crawling crawling : fmKoreaCrawlingDataList) {
////            crawlingDao.saveCrawlingData(crawling); // FM코리아 데이터를 DB에 저장
////        }
////        
////        for (Crawling crawling : ppomppuCrawlingDataList) {
////            crawlingDao.saveCrawlingData(crawling); // 뽐뿌 데이터를 DB에 저장
////        }
//    }
//    
//    
//
//    
//
//    // 마다 실행되도록 스케줄링 설정
//    @Scheduled(fixedRate = 30000)
//    public void deleteCrawlingData() {
//        // 현재 시간 기준으로 초 이전의 데이터를 삭제
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND, -30);
//        Date timestamp = calendar.getTime();
//        
//        crawlingDao.deleteByCreatedAtBefore(timestamp);
//        System.out.println("Crawling data deleted at: " + new Date());
//    }
    
	
}

