package by.itac.project02.service.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.itac.project02.bean.InfoAboutUpdatedNews;
import by.itac.project02.bean.NewsData;
import by.itac.project02.dao.NewsDAO;
import by.itac.project02.dao.NewsDAOException;
import by.itac.project02.service.NewsService;
import by.itac.project02.service.ServiceException;
import by.itac.project02.service.validation.NewsValidationException;
import by.itac.project02.service.validation.NewsValidationService;
import by.itac.project02.util.Constant;

@Service
public class NewsServiceImpl implements NewsService {

	@Autowired
	private NewsDAO newsDAO;

	@Autowired
	private NewsValidationService newsValidationService;

	@Override
	@Transactional
	public int save(NewsData news, int reporterID) throws ServiceException, NewsValidationException {
		if (!newsValidationService.addNewsDataValidation(news)) {
			throw new NewsValidationException("Error news validation");
		}

		if (!newsValidationService.isNumberValidation(reporterID)) {
			throw new NewsValidationException("Error id reporter validation");
		}

		try {

			return newsDAO.addNews(news, reporterID);
		} catch (NewsDAOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public NewsData findById(int id) throws ServiceException, NewsValidationException {
		if (!newsValidationService.isNumberValidation(id)) {
			throw new NewsValidationException("Error id news validation");
		}

		try {
			return newsDAO.findById(id);
		} catch (NewsDAOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public List<NewsData> latestList(int count) throws ServiceException, NewsValidationException {
		if (!newsValidationService.isNumberValidation(count)) {
			throw new NewsValidationException("Error count news validation");
		}

		try {
			return newsDAO.latestsList(count);
		} catch (NewsDAOException e) {
			throw new ServiceException(e);
		}

	}

	@Override
	@Transactional
	public List<NewsData> newsListByPageNumber(int pageNumber) throws ServiceException, NewsValidationException {
		if (!newsValidationService.isNumberValidation(pageNumber)) {
			throw new NewsValidationException("Error page number validation");
		}

		try {
			int countOfAllNews;
			int maxNewsNumberPerPage;

			countOfAllNews = newsDAO.countOfNews();
			maxNewsNumberPerPage = Constant.MAX_NEWS_NUMBER_PER_PAGE;

			if (countOfAllNews < maxNewsNumberPerPage) {
				maxNewsNumberPerPage = countOfAllNews;
			}

			int skip = (pageNumber - 1) * maxNewsNumberPerPage;

			return newsDAO.newsListForOnePage(skip, maxNewsNumberPerPage);
		} catch (NewsDAOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public List<Integer> pageList() throws ServiceException {

		try {
			CopyOnWriteArrayList<Integer> pageList = new CopyOnWriteArrayList<>();
			double newsNumber;
			double pageNumber;

			newsNumber = newsDAO.countOfNews();
			pageNumber = newsNumber / Constant.MAX_NEWS_NUMBER_PER_PAGE;

			int i = 0;
			while (i < pageNumber) {
				pageList.add(i + 1);
				i++;
			}
			return pageList;

		} catch (NewsDAOException e) {
			throw new ServiceException(e);
		}

	}

	@Override
	@Transactional
	public void updateNews(InfoAboutUpdatedNews info, int reporterID, NewsData news)
			throws ServiceException, NewsValidationException {

		if (!newsValidationService.isNumberValidation(reporterID)) {
			throw new NewsValidationException("Error id reporter validation");
		}

		if (!newsValidationService.addNewsDataValidation(news)) {
			throw new NewsValidationException("Error in edit news process");
		}

		try {
			newsDAO.updateNews(info, reporterID, news);

		} catch (NewsDAOException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public void deleteNews(String[] idNewsArrStr) throws ServiceException, NewsValidationException {

		if (!newsValidationService.newsIdValidation(idNewsArrStr)) {
			throw new NewsValidationException("Error id news validation");
		}

		int[] idNewsArrInt = new int[idNewsArrStr.length];

		int i = 0;
		for (String idNews : idNewsArrStr) {
			idNewsArrInt[i] = Integer.parseInt(idNews);
			i++;
		}

		try {
			newsDAO.deleteNews(idNewsArrInt);
		} catch (NewsDAOException e) {
			throw new ServiceException(e);
		}
	}

}
