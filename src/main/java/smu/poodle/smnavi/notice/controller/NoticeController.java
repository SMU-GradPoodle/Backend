package smu.poodle.smnavi.notice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import smu.poodle.smnavi.notice.domain.NoticeEntity;
import smu.poodle.smnavi.notice.dto.NoticeDto;
import smu.poodle.smnavi.notice.service.NoticeService;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    @PostMapping("/api/notice") //글 작성
    public ResponseEntity<Object>addNotice(@RequestBody @Valid NoticeDto noticeDto, Errors errors){
        noticeService.addNotice(noticeDto);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/api/notice") //공지사항 글 전체 조회
    public ResponseEntity<?>getAllNotices(@RequestParam(required = false) String keyword){
        List<NoticeDto>noticeDtos = noticeService.listAllNotice(keyword);
        Map<String,Object> result = new HashMap<>();
        result.put("data",noticeDtos);
        result.put("count",noticeDtos.size());

        return ResponseEntity.ok().body(result);
    }
    @GetMapping("/api/notice/{id}") //id 별로 공지사항 조회
    public ResponseEntity<?>getNoticeById(@PathVariable(value = "id") Long id){ //noticedto객체의 id값으로 매핑됨
        Optional<NoticeDto> noticeDtoId = noticeService.getNoticeById(id);
        if(noticeDtoId.isPresent()){
            noticeService.increaseViews(id);
            return ResponseEntity.ok().body(noticeDtoId.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/api/notice/{id}") //공지사항 한개씩 삭제
    public ResponseEntity<?>deleteId(@PathVariable(value = "id") Long id){
        Long deleteId2 = noticeService.deleteId(id);
        if (deleteId2==null){
            return ResponseEntity.ok().body(deleteId2);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    //공지사항 수정
    @PutMapping("/api/notice/{id}")
    public ResponseEntity<?>updateNotice(@PathVariable(value = "id")Long id, @RequestBody NoticeDto noticeDto){
        Optional<NoticeEntity> updateNotice = noticeService.updateNotice(id, noticeDto);
        if(updateNotice.isPresent()){
            return ResponseEntity.ok().body(updateNotice);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
