package cn.qs.controller.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.qs.bean.common.Picture;
import cn.qs.service.common.PictureService;
import cn.qs.utils.UUIDUtils;
import cn.qs.utils.file.FileHandleUtil;

@Controller
@RequestMapping("blogPicture")
public class PictureController {

	private static final Logger logger = LoggerFactory.getLogger(PictureController.class);

	@Autowired
	private PictureService pictureService;

	/**
	 * Restful风格获取图片
	 * 
	 * @param request
	 * @param response
	 * @param pictureId
	 */
	@RequestMapping("/getPicture/{pictureId}")
	public void getPicture(HttpServletRequest request, HttpServletResponse response, @PathVariable() String pictureId) {
		FileInputStream in = null;
		ServletOutputStream outputStream = null;
		try {
			String picturePath = pictureService.getPicturePathByPictureId(pictureId);
			File fileByName = FileHandleUtil.getFileByName(picturePath);
			in = new FileInputStream(fileByName);
			outputStream = response.getOutputStream();
			IOUtils.copyLarge(in, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(outputStream);
		}
	}

	/**
	 * 图片上传
	 * 
	 * @param imgFile
	 * @return
	 */
	@RequestMapping("/uploadPicture")
	@ResponseBody
	public Map<String, Object> uploadPicture(MultipartFile imgFile) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("error", 1);

		if (imgFile == null) {
			result.put("message", "文件没接到");
			return result;
		}
		logger.debug("file -> {},viewId ->{}", imgFile.getOriginalFilename());

		String fileOriName = imgFile.getOriginalFilename();// 获取原名称
		String fileNowName = UUIDUtils.getUUID2() + "." + FilenameUtils.getExtension(fileOriName);// 生成唯一的名字
		try {
			FileHandleUtil.uploadSpringMVCFile(imgFile, fileNowName);

		} catch (Exception e) {
			logger.error("uploadPicture error", e);
			return result;
		}

		String id = UUIDUtils.getUUID();
		Picture picture = new Picture();
		picture.setCreatetime(new Date());
		picture.setPath(fileNowName);
		picture.setId(id);

		pictureService.insert(picture);

		// 回传JSON结果
		result.put("error", 0);
		result.put("url", "/blogPicture/getPicture/" + id);
		return result;
	}
}