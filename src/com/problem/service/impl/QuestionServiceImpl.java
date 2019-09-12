package com.problem.service.impl;
import java.io.File;
import java.util.StringTokenizer;
import org.apache.ibatis.session.SqlSession;
import com.common.utils.SqlsessionFactoryUtils;
import com.problem.conf.CommonConfig;
import com.problem.daoMapper.QuestionDaoMapper;
import com.problem.entity.Question;
import com.problem.service.QuestionService;
 

public class QuestionServiceImpl implements  QuestionService{

	@Override
	public void deleteQuestion(int [] ids) {
		//QuestionDaoMapper   qd = new QuestionDaoImpl();
		SqlSession openSession = new SqlsessionFactoryUtils().getSqlSessionFactory().openSession();
		QuestionDaoMapper mapper = openSession.getMapper(QuestionDaoMapper.class);
		StringBuilder idlist_str = new StringBuilder();
		//1 删除文件系统中图片。
		for(int id : ids) {
			idlist_str.append(id).append(",");
			Question q = mapper.findById(id);
			if(q != null ) {
				deleteFileFromDisk(q.getQimg());
				deleteFileFromDisk(q.getAnimg());
			}
		}
		 idlist_str.deleteCharAt( idlist_str.length() - 1 );
		//2 再删除数据库。
		mapper.deleteByIdList(ids);
		openSession.commit();
		
	}

	 private static void deleteFileFromDisk(String imgslist) {
		 if( imgslist != null && !"".equals( imgslist )) {
			 StringTokenizer stk = new StringTokenizer(imgslist, "#");
			 while(stk.hasMoreTokens()) {
				 //将虚拟目录部分，替换为 ""
				 String dir = stk.nextToken();
				 String real_disk_path = CommonConfig.BASE_PATH +  dir.replaceFirst(CommonConfig.PRE_FIX, "");
				 File f = new File(real_disk_path);
				 if(f.exists()) {
					 f.delete();
				 }
			 }
		 }
	 }
	 
}
