package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Quotation;
import tw.com.masterhand.gmorscrm.room.record.QuotationProduct;

public class QuotationWithConfig extends BaseTrip {
    Quotation quotation;
    List<File> files;
    List<Contacter> contacters;
    List<QuotationProduct> products;

    public QuotationWithConfig() {
        super();
        trip.setType(TripType.QUOTATION);
        products = new ArrayList<>();
        contacters = new ArrayList<>();
        files = new ArrayList<>();
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
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

    public ContractWithConfig transferToContract() {
        ContractWithConfig contractWithConfig = new ContractWithConfig();
        contractWithConfig.getContract().setAmount(quotation.getAmount());
        contractWithConfig.getContract().setPayment_method(quotation.getPayment_method());
        contractWithConfig.getContract().setUnit_type(quotation.getUnit_type());
        contractWithConfig.getTrip().setCustomer_id(getTrip().getCustomer_id());
        contractWithConfig.getTrip().setProject_id(getTrip().getProject_id());
        contractWithConfig.getTrip().setDescription(getTrip().getDescription());
        contractWithConfig.setFiles(files);
        for (Contacter contacter : contacters) {
            contacter.setId("");
        }
        contractWithConfig.setContacters(contacters);
        for (QuotationProduct product : products) {
            product.setId("");
        }
        contractWithConfig.setProducts(products);
        return contractWithConfig;
    }
}
