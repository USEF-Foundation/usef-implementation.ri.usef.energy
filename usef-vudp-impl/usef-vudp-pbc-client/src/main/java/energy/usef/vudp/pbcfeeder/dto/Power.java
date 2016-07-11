package energy.usef.vudp.pbcfeeder.dto;

/**
 * An object to hold the power values for a device.
 */
public class Power {
    public Long consumption;
    public Long production;
    public Long flexConsumption;
    public Long flexProduction;

    public Power() {
    }

    public Power(Long consumption, Long production, Long flexConsumption, Long flexProduction) {
        this.consumption = consumption;
        this.production = production;
        this.flexConsumption = flexConsumption;
        this.flexProduction = flexProduction;
    }

    public void setConsumption(Long consumption) {
        this.consumption = consumption;
    }

    public void setProduction(Long production) {
        this.production = production;
    }

    public void setFlexConsumption(Long flexConsumption) {
        this.flexConsumption = flexConsumption;
    }

    public void setFlexProduction(Long flexProduction) {
        this.flexProduction = flexProduction;
    }

    public Long getConsumption() {
        return consumption;
    }

    public Long getProduction() {
        return production;
    }

    public Long getFlexConsumption() {
        return flexConsumption;
    }

    public Long getFlexProduction() {
        return flexProduction;
    }
}
