package energy.usef.vudp.pbcfeeder.model;

/**
 *
 */
public class Power {
    public Long consumption;
    public Long production;
    public Long flexConsumption;
    public Long flexProduction;

    public Power(Long consumption, Long production, Long flexConsumption, Long flexProduction) {
        this.consumption = consumption;
        this.production = production;
        this.flexConsumption = flexConsumption;
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
