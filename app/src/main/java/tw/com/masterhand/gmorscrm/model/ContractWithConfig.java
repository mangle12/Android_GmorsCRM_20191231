package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.Contract;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.QuotationProduct;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCurrency;
import tw.com.masterhand.gmorscrm.room.setting.CurrencyRate;

public class ContractWithConfig extends BaseTrip {
    Contract contract;
    List<File> files;
    List<Contacter> contacters;
    List<QuotationProduct> products;

    public ContractWithConfig() {
        super();
        contract = new Contract();
        trip.setType(TripType.CONTRACT);
        products = new ArrayList<>();
        contacters = new ArrayList<>();
        files = new ArrayList<>();
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Contacter> getContacters() {
        return contacters;
    }

    public void setContacters(List<Contacter> contacters) {
        this.contacters = contacters;
    }

    public List<QuotationProduct> getProducts() {
        return products;
    }

    public void setProducts(List<QuotationProduct> products) {
        this.products = products;
    }
}
