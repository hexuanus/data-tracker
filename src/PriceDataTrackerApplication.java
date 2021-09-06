import client.PriceClient;
import manager.PriceManager;
import resources.PriceResource;
import io.dropwizard.setup.Environment;

public class PriceDataTrackerApplication {
    private PriceManager priceManager;
    private PriceClient priceClient;
    private Environment environment;

    public static void main(String[] args) throws Exception {
        new PriceDataTrackerApplication().run();
    }

    public void run() throws Exception {
        this.environment = new Environment();
        this.priceClient = new PriceClient();
        this.priceManager = new PriceManager(priceClient);
        priceManager.start();

        environment.jersey().register(new PriceResource(priceManager));
    }
}
