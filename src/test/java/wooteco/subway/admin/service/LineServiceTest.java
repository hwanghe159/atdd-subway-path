package wooteco.subway.admin.service;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Edge;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineDetailResponse;
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    private static final String STATION_NAME1 = "강남역";
    private static final String STATION_NAME2 = "역삼역";
    private static final String STATION_NAME3 = "선릉역";
    private static final String STATION_NAME4 = "삼성역";

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private LineService lineService;

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);

        station1 = new Station(1L, STATION_NAME1);
        station2 = new Station(2L, STATION_NAME2);
        station3 = new Station(3L, STATION_NAME3);
        station4 = new Station(4L, STATION_NAME4);

        line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        line.addEdge(new Edge(null, 1L, 10, 10));
        line.addEdge(new Edge(1L, 2L, 10, 10));
        line.addEdge(new Edge(2L, 3L, 10, 10));
    }

    @Test
    void addEdgeAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

        EdgeCreateRequest request = new EdgeCreateRequest(null, station4.getId(), 10, 10);
        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdges()).hasSize(4);

        List<Long> stationIds = line.getEdgesId();
        assertThat(stationIds.get(0)).isEqualTo(4L);
        assertThat(stationIds.get(1)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(2L);
        assertThat(stationIds.get(3)).isEqualTo(3L);
    }

    @Test
    void addEdgeBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

        EdgeCreateRequest request = new EdgeCreateRequest(station1.getId(), station4.getId(), 10, 10);
        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdges()).hasSize(4);

        List<Long> stationIds = line.getEdgesId();
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(1)).isEqualTo(4L);
        assertThat(stationIds.get(2)).isEqualTo(2L);
        assertThat(stationIds.get(3)).isEqualTo(3L);
    }

    @Test
    void addEdgeAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

        EdgeCreateRequest request = new EdgeCreateRequest(station3.getId(), station4.getId(), 10, 10);
        lineService.addEdge(line.getId(), request);

        assertThat(line.getEdges()).hasSize(4);

        List<Long> stationIds = line.getEdgesId();
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(1)).isEqualTo(2L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
        assertThat(stationIds.get(3)).isEqualTo(4L);
    }

    @Test
    void removeEdgeAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 1L);

        assertThat(line.getEdges()).hasSize(2);

        List<Long> stationIds = line.getEdgesId();
        assertThat(stationIds.get(0)).isEqualTo(2L);
        assertThat(stationIds.get(1)).isEqualTo(3L);
    }

    @Test
    void removeEdgeBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 2L);

        List<Long> stationIds = line.getEdgesId();
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(1)).isEqualTo(3L);
    }

    @Test
    void removeEdgeAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeEdge(line.getId(), 3L);

        assertThat(line.getEdges()).hasSize(2);

        List<Long> stationIds = line.getEdgesId();
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(1)).isEqualTo(2L);
    }

    @Test
    void findLineWithStationsById() {
        List<Station> stations = Lists.newArrayList(new Station("강남역"), new Station("역삼역"), new Station("삼성역"));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        LineDetailResponse lineDetailResponse = lineService.findLineWithStationsById(1L);

        assertThat(lineDetailResponse.getStations()).hasSize(3);
    }

    @DisplayName("전체 노선 조회 테스트")
    @Test
    void wholeLinesTest() {
        Line newLine = new Line(2L, "신분당선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        newLine.addEdge(new Edge(null, 4L, 10, 10));
        newLine.addEdge(new Edge(4L, 5L, 10, 10));
        newLine.addEdge(new Edge(5L, 6L, 10, 10));

        List<Station> stations = Arrays.asList(new Station(1L, "강남역"), new Station(2L, "역삼역"),
                new Station(3L, "삼성역"), new Station(4L, "양재역")
                , new Station(5L, "양재시민의숲역"), new Station(6L, "청계산입구역"));
        when(lineRepository.findAll()).thenReturn(Arrays.asList(this.line, newLine));
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        List<LineDetailResponse> responses = lineService.wholeLines().getLineDetailResponses();

        assertThat(responses).isNotNull();
        assertThat(responses.get(0).getStations().size()).isEqualTo(3);
        assertThat(responses.get(1).getStations().size()).isEqualTo(3);
    }
}
