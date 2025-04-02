package at.jku.se.lunchify.models;

public class InvoiceSetting {

    private int settingid;
    private double valueinvoicesupermarket;
    private double valueinvoicerestaurants;

    public InvoiceSetting(int settingid, double valueinvoicesupermarket, double valueinvoicerestaurants) {
        this.settingid = settingid;
        this.valueinvoicesupermarket = valueinvoicesupermarket;
        this.valueinvoicerestaurants = valueinvoicerestaurants;
    }

    public int getSettingid() {
        return settingid;
    }

    public void setSettingid(int settingid) {
        this.settingid = settingid;
    }

    public double getValueinvoicesupermarket() {
        return valueinvoicesupermarket;
    }

    public void setValueinvoicesupermarket(double valueinvoicesupermarket) {
        this.valueinvoicesupermarket = valueinvoicesupermarket;
    }

    public double getValueinvoicerestaurants() {
        return valueinvoicerestaurants;
    }

    public void setValueinvoicerestaurants(double valueinvoicerestaurants) {
        this.valueinvoicerestaurants = valueinvoicerestaurants;
    }

    @Override
    public String toString() {
        return "InvoiceSetting{" +
                "settingid=" + settingid +
                ", valueinvoicesupermarket=" + valueinvoicesupermarket +
                ", valueinvoicerestaurants=" + valueinvoicerestaurants +
                '}';
    }
}
