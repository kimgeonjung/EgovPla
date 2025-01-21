package pla.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import pla.entity.DataBoard;
import pla.service.DataBoardService;
import pla.utils.PagingUtils;

@Controller
@RequiredArgsConstructor
public class DataBoardController {
    private final DataBoardService dataBoardService;


    @GetMapping("/dataList")
    public String getDataList(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        List<DataBoard> dataBoard = dataBoardService.getAllDataList();
        Page<DataBoard> dataBoardPage = PagingUtils.createPage(dataBoard, pageable);

        model.addAttribute("dataList", dataBoardPage);
        model.addAttribute("totalDataList", dataBoard.size());
        model.addAttribute("now", LocalDateTime.now());
        return "data/dataList";
    }
    
    @GetMapping("/dataListDetail")
    public String getDataListDetail(@RequestParam Long id,
                                    @RequestParam(defaultValue = "10") int limit,
                                    @RequestParam(defaultValue = "0") int offset,Model model) throws Exception {

        DataBoard dataBoard = dataBoardService.getDataListDetail(id, true, limit, offset);
        model.addAttribute("dataList", dataBoard);
        model.addAttribute("now", LocalDateTime.now()); // 현재 시간
        return "data/dataListDetail";
    }
}
