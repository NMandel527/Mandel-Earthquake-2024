package mandel.earthquake;

import mandel.earthquake.json.FeatureCollection;
import mandel.earthquake.json.Properties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EarthquakeServiceTest
{
    @Test
    void oneHour()
    {
        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        //when
        FeatureCollection collection = service.oneHour().blockingGet(); //only use blockingGet in tests

        //then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }

    @Test
    void oneMonth()
    {
        //given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        //when
        FeatureCollection collection = service.oneMonth().blockingGet(); //only use blockingGet in tests

        //then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }
}