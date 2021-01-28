package subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.convertor.LineConvertor;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineResponseWithStation;
import subway.dto.SectionRequest;
import subway.factory.LineFactory;
import subway.factory.SectionFactory;
import subway.service.LineService;
import subway.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Section section = new Section(stationService.findOne(lineRequest.getUpStationId()),
                stationService.findOne(lineRequest.getDownStationId()),
                lineRequest.getDistance());
        LineResponse lineResponse = LineConvertor.convertLine(lineService.save(LineFactory.getLine(lineRequest), section));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(LineConvertor.convertLines(lineService.findAll()));
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponseWithStation> showLine(@PathVariable Long lineId) {
        return ResponseEntity.ok(LineConvertor.convertLineWithResponse(lineService.findOne(lineId)));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.update(LineFactory.getLine(lineId, lineRequest));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineService.deleteById(lineId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        lineService.saveSection(SectionFactory.getSection(sectionRequest, lineId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteSection(lineId, stationService.findOne(stationId));
        return ResponseEntity.ok().build();
    }
}
