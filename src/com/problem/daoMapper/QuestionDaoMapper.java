package com.problem.daoMapper;
import java.util.List;
import com.problem.entity.Question;
import com.problem.vo.QuestionVO;
import com.problem.vo.UpdateQuestionVO;
import com.problem.vo.UpdateStatusVO;

public interface QuestionDaoMapper {

	//2��ѯ���� 3����ѯ��  page ,pagesize =>  limit page*pageSize,pageSize;
	//���أ�{total:20,data:[{},{}]}
	/**
	 * ���ݷ�����ҡ�����ҳ
	 * @return
	 */
	//Map<String,Object> getByType(String type,int page,int pageSize);
	
	//Map<String,Object> getByKeyWord(String keyword,int page,int pageSize);
	
	//4����״̬������id����status��ok
	void updateStatus(UpdateStatusVO vo);
	/**
	 * ��������id���´𰸡�
	 * @param id
	 * @param status
	 */
	void updateAnswerById(int id,String answer,String animg);
	
	//ResultData getAllByPage(Page page);
	
	List<Question> getByCondition(QuestionVO qvo);
	
	/**
	 * ���´𰸡�״̬��
	 * @param updatevo
	 */
	void udpateAnswer(UpdateQuestionVO updatevo);
	//3ɾ�����⡣����id��ok
	void deleteById(int id);
		//ok
		// ids = 1,2,3,4
	void deleteByIdList(int[]  ids );
		
	Question findById(int id);
	//1�������  ok
	void insert(Question q);
}
