package com.cos.photogramstart.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cos.photogramstart.config.auth.PrincipalDetails;
import com.cos.photogramstart.domain.image.Image;
import com.cos.photogramstart.domain.image.ImageRepository;
import com.cos.photogramstart.web.dto.image.ImageUploadDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ImageService {
	private final ImageRepository imageRepository;
	
	@Value("${file.path}") //application.yml 파일에 있는 값을 가져오기 위해, 사용자가 직접 yml에 만들어도 됨
	private String uploadFolder; // 여기에 path가 자동으로 담김
	
	@Transactional
	public void 사진업로드(ImageUploadDto imageUploadDto, PrincipalDetails principalDetails) {
		
		UUID uuid = UUID.randomUUID(); // uuid
		
		String imageFileName = uuid+"_"+imageUploadDto.getFile().getOriginalFilename(); // 1.jpg, 동일한 파일명 들어오면 어떡해 -> UUID
		//System.out.println("이미지 파일 이름 : "+imageFileName);
		
		Path imageFilePath = Paths.get(uploadFolder + imageFileName);
		
		//통신 or I/O -> 예외가 발생할 수 있다.
		try {
			Files.write(imageFilePath, imageUploadDto.getFile().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//image 테이블에 저장
		Image image = imageUploadDto.toEntity(imageFileName, principalDetails.getUser()); //어차피 앞 폴더 경로는 yml에 있음
		imageRepository.save(image);
		
		//System.out.println(imageEntity);
	}

}