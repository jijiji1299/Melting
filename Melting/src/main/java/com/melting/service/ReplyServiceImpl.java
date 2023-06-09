package com.melting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.melting.dao.ReplyDAO;
import com.melting.domain.Reply;

@Service
public class ReplyServiceImpl implements ReplyService {
	
	@Autowired
	ReplyDAO replyDao;

	@Override
	public int writeReply(Reply reply) {
		int result = replyDao.writeReply(reply);
		return result;
	}

	@Override
	public List<Reply> listReply(int boardseq) {
		return replyDao.listReply(boardseq);
	}

	@Override
	public int deleteReply(int replyseq) {
		int result = replyDao.deleteReply(replyseq);
		return result;
	}

	@Override
	public void updateReplyCount(int boardseq) {
		replyDao.updateReplyCount(boardseq);
		
	}

	@Override
	public void downReplyCount(int boardseq) {
		replyDao.downReplyCount(boardseq);
		
	}


}
