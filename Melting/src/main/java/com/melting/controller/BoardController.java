package com.melting.controller;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.melting.crawling.CrawlingService;
import com.melting.domain.Board;
import com.melting.domain.Crawling;
import com.melting.domain.Member;
import com.melting.domain.Reply;
import com.melting.service.BoardService;
import com.melting.service.MemberService;
import com.melting.service.ReplyService;
import com.melting.util.FileService;
import com.melting.util.MarkdownConverter;

@Controller
public class BoardController{
	private final CrawlingService crawlingService;
	private final ReplyService replyService;
	private final MemberService memberService;
	
	@Autowired
	private BoardService boardService;
	
	public BoardController(CrawlingService crawlingService, ReplyService replyService, MemberService memberService) {
        this.crawlingService = crawlingService;
        this.replyService = replyService;
        this.memberService = memberService;
    }
	
	@Value("${spring.servlet.multipart.location}")
	String uploadPath;
	
	/*메인 화면 요청*/
	@GetMapping({"/", ""})
	public String main(Model model, Authentication authentication) throws IOException {
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			model.addAttribute("membername", membername);
		}
		
		// 조회순으로 정렬(10개)
		List<Crawling> viewscntSortedList = crawlingService.getViewscntSortedList();
		if (viewscntSortedList.size() > 10) {
			viewscntSortedList = viewscntSortedList.subList(0, 10);
		}
		model.addAttribute("viewscntSortedList", viewscntSortedList);
		
		
		// Best 카테고리(조회순)
		List<Crawling> viewsSortedBestList = crawlingService.getViewsSortedBestList();
		if (viewsSortedBestList.size() >10) {
			viewsSortedBestList = viewsSortedBestList.subList(0, 10);
		}
		model.addAttribute("viewsSortedBestList", viewsSortedBestList);
		
		
		// Humor 카테고리 (조회순)
		List<Crawling> viewsSortedHumorList = crawlingService.getViewsSortedHumorList();
		if (viewsSortedHumorList.size() >10) {
			viewsSortedHumorList = viewsSortedHumorList.subList(0, 10);
		}
		model.addAttribute("viewsSortedHumorList", viewsSortedHumorList);
		
		
		// Game 카테고리 (조회순)
		List<Crawling> viewsSortedGameList = crawlingService.getViewsSortedGameList();
		if (viewsSortedGameList.size() >10) {
			viewsSortedGameList = viewsSortedGameList.subList(0, 10);
		}
		model.addAttribute("viewsSortedGameList", viewsSortedGameList);

		
		// Sports 카테고리 (조회순)
		List<Crawling> viewsSortedSportsList = crawlingService.getViewsSortedSportsList();
		if (viewsSortedSportsList.size() >10) {
			viewsSortedSportsList = viewsSortedSportsList.subList(0, 10);
		}
		model.addAttribute("viewsSortedSportsList", viewsSortedSportsList);
		
		
		// 크롤링 검색어순위, hit
        List<Crawling> dcSearchList = crawlingService.getDcSearchCrawlingData();
        List<Crawling> hitList = crawlingService.getHitCrawlingData();

        model.addAttribute("dcSearchList", dcSearchList);
        model.addAttribute("hitList", hitList);
        
        
        
		return "/main";
	}
	
	/*게시글 '홈-Best' 목록 화면 요청*/
	@GetMapping("/board/bestlist")
	public String bestlist(Model model, Authentication authentication) {
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			String memberid = member.getMemberid();
			
			model.addAttribute("membername", membername);
			model.addAttribute("memberid", memberid);
		}
		
		
		// 작성글 Best 카테고리(조회순) - 최대 6개 제한
		List<Board> writedBestList = boardService.getWritedBestList();
		if (writedBestList.size() > 4) {
			writedBestList = writedBestList.subList(0, 4);
		}
		model.addAttribute("writedBestList", writedBestList);
		
		// 크롤링 Best 카테고리(조회순)
		List<Crawling> viewsSortedBestList = crawlingService.getViewsSortedBestList();
		model.addAttribute("viewsSortedBestList", viewsSortedBestList);
		
		return "/board/bestlist";
	}
	
	
	/*게시글 쓰기 화면 요청*/
	@GetMapping("/write")
	public String write(Model model, Member member, Authentication authentication) {
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			model.addAttribute("membername", membername);
		}
		
		model.addAttribute("member", member);
		return "/board/write";
	}
	
	/*게시글 저장 요청*/
	@PostMapping("/write")
	public String write(Board board, String boardtxt, MultipartFile upload) {
		
		// 마크다운 적용
		String markdownContent = boardtxt;
        String htmlContent = MarkdownConverter.convertMarkdownToHtml(markdownContent);
        board.setContent(htmlContent);
        
        // 파일 업로드
		String originalFilename= null;
		String savedFileName = null;
		
		if(upload != null && !upload.isEmpty()) {
		
		
			originalFilename = upload.getOriginalFilename();
			savedFileName=FileService.savedFile(upload, uploadPath);
			
	        // 파일이 성공적으로 저장되었을 경우에만 파일명을 Board에 세팅
	        if (savedFileName != null) {
	            board.setOriginalfile(originalFilename);
	            board.setSavedfile(savedFileName);
	        }
	        
		}
		
		int result = boardService.write(board);
		
		return "redirect:/board/newlist";
		
	}
	
	/*게시글 '전체' 목록 화면 요청*/
	@GetMapping("/board/newlist")
	public String boardlist(Model model, Authentication authentication) {
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			model.addAttribute("membername", membername);
		}

		// 작성글 전체(최신순)
		List<Board> list = boardService.getAllList();
		model.addAttribute("list", list);
		
		// 크롤링 전체(최신순)
		List<Crawling> crawlingList = crawlingService.getCrawlingList();
		model.addAttribute("crawlingList", crawlingList);
		
		
		return "/board/newlist";
	}
	
	
	/*게시글 '유머' 목록 화면 요청*/
	@GetMapping("/board/humorlist")
	public String humorlist(Model model, Authentication authentication) {
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			model.addAttribute("membername", membername);
		}
		
		// 작성글 유머 카테고리(조회순)
		List<Board> writedHumorList = boardService.getWritedHumorList();
		model.addAttribute("writedHumorList", writedHumorList);
		
		
		// 크롤링 유머 카테고리(조회순)
		List<Crawling> viewsSortedHumorList = crawlingService.getViewsSortedHumorList();
		model.addAttribute("viewsSortedHumorList", viewsSortedHumorList);
		
		return "/board/humorlist";
	}
	
	
	/*게시글 '게임' 목록 화면 요청*/
	@GetMapping("/board/gamelist")
	public String gamelist(Model model, Authentication authentication) {
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			model.addAttribute("membername", membername);
		}
		
		// 작성글 게임 카테고리(조회순)
		List<Board> writedGameList = boardService.getWritedGameList();
		model.addAttribute("writedGameList", writedGameList);
		
		
		// 크롤링 게임 카테고리(조회순)
		List<Crawling> viewsSortedGameList = crawlingService.getViewsSortedGameList();
		model.addAttribute("viewsSortedGameList", viewsSortedGameList);
		
		return "/board/gamelist";
	}
	
	/*게시글 '스포츠' 목록 화면 요청*/
	@GetMapping("/board/sportslist")
	public String sportslist(Model model, Authentication authentication) {
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			model.addAttribute("membername", membername);
		}
		
		// 작성글 스포츠 카테고리(조회순)
		List<Board> writedSportsList = boardService.getWritedSportsList();
		model.addAttribute("writedSportsList", writedSportsList);
		
		
		// 크롤링 스포츠 카테고리(조회순)
		List<Crawling> viewsSortedSportsList = crawlingService.getViewsSortedSportsList();
		model.addAttribute("viewsSortedSportsList", viewsSortedSportsList);
		
		return "/board/sportslist";
	}
	

	/*게시글 읽기 화면 요청*/
	@GetMapping("/read")
	public String read(int boardseq, Model model, Authentication authentication) {
		Board board = boardService.read(boardseq);
		
		// 조회수 증가
		boardService.updateViewsCount(boardseq);
		
		// 추천글 목록 10개 출력
		List<Board> recommendlist = boardService.getRecommendList(boardseq);
		if (recommendlist.size() > 10) {
		    recommendlist = recommendlist.subList(0, 10);
		}
		
		model.addAttribute("board", board);
		model.addAttribute("recommendlist", recommendlist);
		
		// 댓글 목록 출력
		List<Reply> replylist = replyService.listReply(boardseq);
		model.addAttribute("replylist", replylist);
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			String memberid = member.getMemberid();
			
			model.addAttribute("membername", membername);
			model.addAttribute("memberid", memberid);
		}
		
		
		// 크롤링 Best 카테고리(조회순)
		List<Crawling> viewsSortedBestList = crawlingService.getViewsSortedBestList();
		if (viewsSortedBestList.size() >10) {
			viewsSortedBestList = viewsSortedBestList.subList(0, 10);
		}
		model.addAttribute("viewsSortedBestList", viewsSortedBestList);
		
		
		// 크롤링 Hit 목록
        List<Crawling> hitList = crawlingService.getHitCrawlingData();
        model.addAttribute("hitList", hitList);
		
		return "/board/read";
		
	}
	
	/*게시글 삭제*/
	@GetMapping("/delete")
	public String delete(int boardseq) {
		int result = boardService.delete(boardseq);
		
		
		return "redirect:/board/newlist";
	}
	
	/*게시글 수정 화면 요청*/
	@GetMapping("/update")
	public String update(int boardseq, Model model, Authentication authentication) {
		Board board = boardService.read(boardseq);
		model.addAttribute("board", board);
		
		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			model.addAttribute("membername", membername);
		}
				
		return "/board/update";
	}
	
	
	/*게시글 수정 처리*/
	@PostMapping("/update")
	public String update(String boardtxt, Board board, RedirectAttributes rttr,  MultipartFile upload) {
		
		// 마크다운 적용
		String markdownContent = boardtxt;
	    String htmlContent = MarkdownConverter.convertMarkdownToHtml(markdownContent);
	    board.setContent(htmlContent);
		
	    // 파일 업로드
		String originalFilename= null;
		String savedFileName = null;
		
		if(upload != null && !upload.isEmpty()) {
		
			originalFilename = upload.getOriginalFilename();
			savedFileName=FileService.savedFile(upload, uploadPath);
			
	        // 파일이 성공적으로 저장되었을 경우에만 파일명을 Board에 세팅
	        if (savedFileName != null) {
	            board.setOriginalfile(originalFilename);
	            board.setSavedfile(savedFileName);
	        }
	        
		}
     		
		int result = boardService.update(board);
		rttr.addAttribute("boardseq", board.getBoardseq());
		return "redirect:/board/newlist";
	}
	

	
	/*첨부파일 다운로드*/
	@GetMapping("/board/download")
	public void download(int boardseq, HttpServletResponse response) {
		Board board = boardService.read(boardseq);
		
		// 기본 파일명
		String originalFileName = board.getOriginalfile();
		
		// encode에 마우스 오버해서 surround 클릭
		if (originalFileName==null) {
			originalFileName="이름 없음";
		}else {
			try {
				response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(originalFileName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				originalFileName="파일없음";
				e.printStackTrace();
				
			}
		}
		
		// 저장된 파일명
		String savedFileName = board.getSavedfile();
		String fullPath = uploadPath + "/" +savedFileName;
		
		// 스트림 설정(하드디스크에서 입력스트림, 클라이언트에게 전달할 출력 스트림)
		FileInputStream filein = null;
		ServletOutputStream fileout = null;
		
		try {
			filein = new FileInputStream(fullPath);
			fileout = response.getOutputStream();
			
			FileCopyUtils.copy(filein, fileout);
			
			filein.close();
			fileout.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*추천수 증가*/
	@PostMapping("/updatelikecount")
	public String updatelikecount(int boardseq, Board board) {
		boardService.updateLikeCount(boardseq);
		
		return "redirect:/read?boardseq="+board.getBoardseq();
	}
	
	
	/*검색 기능*/
	@PostMapping("/search")
	public String search(@RequestParam(value="searchword") String searchword, Model model, Authentication authentication) {
	
		
		// 검색
		List<Board> searchWrited = boardService.search(searchword);
		List<Crawling> searchCrawling = crawlingService.search(searchword);
		
		model.addAttribute("searchWrited", searchWrited);
		model.addAttribute("searchCrawling", searchCrawling);
		model.addAttribute("searchword", searchword);

		// 유저이름 불러오기 (membername)
		if (authentication != null) {
			String username = authentication.getName();
			Member member = memberService.getMemberUsername(username);
			String membername = member.getMembername();
			String memberid = member.getMemberid();
			
			model.addAttribute("membername", membername);
			model.addAttribute("memberid", memberid);
		}
		return "/board/search";
	}

}
