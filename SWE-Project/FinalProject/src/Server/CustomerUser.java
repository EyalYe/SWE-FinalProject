package Server;

public class CustomerUser extends User{
    private String cardNumber;
    private String cardExpiration;
    private String cardCVV;

    public CustomerUser(String username, String password, String email, String phone, String address, String cardNumber, String cardExpiration, String cardCVV){
        super(username, password, email, phone, address);
        this.cardNumber = cardNumber;
        this.cardExpiration = cardExpiration;
        this.cardCVV = cardCVV;
    }

    private String getCardNumber(){
        return cardNumber;
    }

    private String getCardExpiration(){
        return cardExpiration;
    }

    private String getCardCVV(){
        return cardCVV;
    }

    public void setCardNumber(String cardNumber){
        this.cardNumber = cardNumber;
    }

    public void setCardExpiration(String cardExpiration){
        this.cardExpiration = cardExpiration;
    }

    public void setCardCVV(String cardCVV){
        this.cardCVV = cardCVV;
    }

    }
