package smu.poodle.smnavi.navi.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.navi.domain.mapping.FullPathAndSubPath;
import smu.poodle.smnavi.navi.domain.path.FullPath;
import smu.poodle.smnavi.navi.domain.path.SubPath;
import smu.poodle.smnavi.navi.repository.FullPathAndSubPathRepository;
import smu.poodle.smnavi.navi.repository.FullPathRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminFullPathService {
    private final FullPathRepository fullPathRepository;
    private final FullPathAndSubPathRepository fullPathAndSubPathRepository;

    public void saveFullPathMappingSubPath(FullPath fullPath, List<SubPath> subPathList){

        List<FullPath> persistedFullPath = fullPathAndSubPathRepository.findAllBySubPath(subPathList, subPathList.size());

        if(!persistedFullPath.isEmpty()) {
            log.info("이미 존재하는 경로 정보입니다. FullPath 번호 : {}", persistedFullPath.get(0));
        }
        else {
            fullPathRepository.save(fullPath);

            for (SubPath subPath : subPathList) {
                fullPathAndSubPathRepository.save(FullPathAndSubPath.builder()
                        .fullPath(fullPath)
                        .subPath(subPath)
                        .build());
            }
        }
    }
}
