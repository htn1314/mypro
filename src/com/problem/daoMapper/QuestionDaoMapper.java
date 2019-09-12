package com.problem.daoMapper;
import java.util.List;
import com.problem.entity.Question;
import com.problem.vo.QuestionVO;
import com.problem.vo.UpdateQuestionVO;
import com.problem.vo.UpdateStatusVO;

public interface QuestionDaoMapper {

	//2查询问题 3个查询。  page ,pagesize =>  limit page*pageSize,pageSize;
	//返回：{total:20,data:[{},{}]}
	/**
	 * 根据分类查找。带分页
	 * @return
	 */
	//Map<String,Object> getByType(String type,int page,int pageSize);
	
	//Map<String,Object> getByKeyWord(String keyword,int page,int pageSize);
	
	//4更新状态。根据id更新status。ok
	void updateStatus(UpdateStatusVO vo);
	/**
	 * 根据问题id更新答案。
	 * @param id
	 * @param status
	 */
	void updateAnswerById(int id,String answer,String animg);
	
	//ResultData getAllByPage(Page page);
	
	List<Question> getByCondition(QuestionVO qvo);
	
	/**
	 * 更新答案、状态。
	 * @param updatevo
	 */
	void udpateAnswer(UpdateQuestionVO updatevo);
	//3删除问题。根据id。ok
	void deleteById(int id);
		//ok
		// ids = 1,2,3,4
	void deleteByIdList(int[]  ids );
		
	Question findById(int id);
	//1添加问题  ok
	void insert(Question q);
}
