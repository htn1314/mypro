package com.problem.web;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import org.apache.ibatis.session.SqlSession;
import com.common.utils.SqlsessionFactoryUtils;
import com.problem.ano.Param;
import com.problem.ano.RequestMapping;
import com.problem.ano.ResponseBody;
import com.problem.daoMapper.QuestionDaoMapper;
import com.problem.dto.ResultData;
import com.problem.entity.Question;
import com.problem.entity.User;
import com.problem.service.QuestionService;
import com.problem.service.impl.QuestionServiceImpl;
import com.problem.vo.QuestionVO;
import com.problem.vo.UpdateQuestionVO;
import com.problem.vo.UpdateStatusVO;

@WebServlet("/question/*")
public class QuestionController extends DispatcherServlet {
	
	@RequestMapping("insert.action")//�ѽ��
	@ResponseBody
	public ResultData insert(@Param(isjavabean=true)Question q,HttpSession ss) throws ServletException, IOException {
		ResultData rs = new ResultData();
		User rsu = (User) ss.getAttribute("loginuser");
		if(rsu == null ) {
			rs.setMsg("����δ��½�����ȵ�½��");
			rs.setCode(300);
		}else {
    		  //QuestionDaoMapper qd = new QuestionDaoImpl();
    		 // qd.insert(q);
			 SqlSession openSession = new SqlsessionFactoryUtils().getSqlSessionFactory().openSession();
			 QuestionDaoMapper mapper = openSession.getMapper(QuestionDaoMapper.class);
			 mapper.insert(q);
			 openSession.commit();//�ύ����
    		 rs.setMsg("����ɹ���");
		}
		return rs;
	}
	//�ڶ���̫������,�Ե�
	@RequestMapping("getall.action")
	@ResponseBody
	public ResultData queryByPage(@Param(isjavabean=true) QuestionVO  queryVO) {//page=1&limit=3
		ResultData rd =new ResultData();
		 SqlSession openSession = new SqlsessionFactoryUtils().getSqlSessionFactory().openSession();
		 QuestionDaoMapper mapper = openSession.getMapper(QuestionDaoMapper.class);
		 //��ǰҳ
		 List<Question> byCondition = mapper.getByCondition(queryVO);
		 rd.setData(byCondition);
		 queryVO.setPage(null);//�ֶ��ÿ�
		 List<Question> byCondition2 = mapper.getByCondition(queryVO);
		 rd.setCount(byCondition2.size());//��������
		 return rd;
	}

	
	@RequestMapping("update.action")//�ѽ��
	@ResponseBody
	public ResultData update(@Param(isjavabean=true) UpdateQuestionVO  updatevo) {//page=1&limit=3
		ResultData rd = new ResultData();
		 SqlSession openSession = new SqlsessionFactoryUtils().getSqlSessionFactory().openSession();
		 QuestionDaoMapper mapper = openSession.getMapper(QuestionDaoMapper.class);
		 mapper.udpateAnswer(updatevo);
		 openSession.commit();//�ύ����
		return rd;
	}
	 
	@RequestMapping("delete.action")
	@ResponseBody//�Ѿ����
	public ResultData delete(@Param("deleteids")String deleteids) {//page=1&limit=3
		ResultData rd = new ResultData();
		//ɾ��֮ǰӦ�ð�ͼƬҲ��ɾ���ˡ�����ʹ��service�㡣
		if(deleteids != null && !"".equals( deleteids )) {
			//deleteids ="12,35,65,"
			StringTokenizer stk = new StringTokenizer(deleteids,",");
			int []ids = new int[stk.countTokens()];
			int index = 0;
			while(stk.hasMoreTokens()) {
				ids [index++] =Integer.parseInt( stk.nextToken() );
			}
			QuestionService qs = new QuestionServiceImpl();
			qs.deleteQuestion(ids);
		}
		return rd;
	}
	/***
	 * ����״̬  �ѽ��
	 * @param vo
	 * @return
	 */
	@RequestMapping("updatestatus.action")
	@ResponseBody
	public ResultData updateStatus(@Param(isjavabean=true)UpdateStatusVO vo) {
		ResultData rd = new ResultData();
		//QuestionDaoMapper  qd = new QuestionDaoImpl();
		//qd.updateStatus(vo);
		 SqlSession openSession = new SqlsessionFactoryUtils().getSqlSessionFactory().openSession();
		 QuestionDaoMapper mapper = openSession.getMapper(QuestionDaoMapper.class);
		 mapper.updateStatus(vo);
		 openSession.commit();
		return rd;
	}
	
	
 
}
