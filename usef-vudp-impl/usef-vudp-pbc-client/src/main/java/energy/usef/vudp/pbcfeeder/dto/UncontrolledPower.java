package energy.usef.vudp.pbcfeeder.dto;

/**
 * An object to hold the power values on the uncontrolled level.
 */
public class UncontrolledPower {

    private Long forecast;
    private Long observed;

    public UncontrolledPower() {
    }

    public UncontrolledPower(Long forecast, Long observed) {
        this.forecast = forecast;
        this.observed = observed;
    }

    public Long getForecast() {
        return forecast;
    }

    public void setForecast(Long forecast) {
        this.forecast = forecast;
    }

    public Long getObserved() {
        return observed;
    }

    public void setObserved(Long observed) {
        this.observed = observed;
    }
}
