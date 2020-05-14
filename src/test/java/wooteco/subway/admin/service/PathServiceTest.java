package wooteco.subway.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.PathResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {
    private static final String STATION_NAME1 = "강남역";
    private static final String STATION_NAME2 = "역삼역";
    private static final String STATION_NAME3 = "선릉역";
    private static final String STATION_NAME4 = "양재역";
    private static final String STATION_NAME5 = "양재시민의숲역";

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private PathService pathService;

    private Line line1;
    private Line line2;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;

    @BeforeEach
    void setUp() {
        pathService = new PathService(lineRepository, stationRepository);

        station1 = new Station(1L, STATION_NAME1);
        station2 = new Station(2L, STATION_NAME2);
        station3 = new Station(3L, STATION_NAME3);
        station4 = new Station(4L, STATION_NAME4);
        station5 = new Station(5L, STATION_NAME5);

        line1 = new Line(1L, "1호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        line1.addLineStation(new LineStation(null, 1L, 10, 10));
        line1.addLineStation(new LineStation(1L, 2L, 5, 15));
        line1.addLineStation(new LineStation(2L, 3L, 10, 10));

        line2 = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        line2.addLineStation(new LineStation(null, 1L, 10, 10));
        line2.addLineStation(new LineStation(1L, 4L, 10, 10));
        line2.addLineStation(new LineStation(4L, 5L, 10, 10));
    }

    @DisplayName("같은 호선 내에서의 경로 찾기 수행")
    @Test
    void findPathInSameLine() {
        PathResponse pathResponse = pathService.calculatePath("강남역", "선릉역");

        assertThat(pathResponse.getDistance()).isEqualTo(15);
        assertThat(pathResponse.getDuration()).isEqualTo(25);
        assertThat(pathResponse.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(pathResponse.getStations().get(1).getName()).isEqualTo("역삼역");
        assertThat(pathResponse.getStations().get(2).getName()).isEqualTo("선릉역");
    }

    @DisplayName("다른 호선 내에서의 경로 찾기 수행")
    @Test
    void findPathInDifferentLine() {
        PathResponse pathResponse = pathService.calculatePath("선릉역", "양재시민의숲역");

        assertThat(pathResponse.getDistance()).isEqualTo(35);
        assertThat(pathResponse.getDuration()).isEqualTo(45);
        assertThat(pathResponse.getStations().get(0).getName()).isEqualTo("선릉역");
        assertThat(pathResponse.getStations().get(1).getName()).isEqualTo("역삼역");
        assertThat(pathResponse.getStations().get(2).getName()).isEqualTo("강남역");
        assertThat(pathResponse.getStations().get(3).getName()).isEqualTo("양재역");
        assertThat(pathResponse.getStations().get(4).getName()).isEqualTo("양재시민의숲역");
    }
}
