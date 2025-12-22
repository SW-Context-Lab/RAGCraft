package lab.context.ragcraft.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/db")
public class HealthDbController {

    private final HealthCheckRepository repository;

    public HealthDbController(HealthCheckRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String dbHealth() {
        HealthCheck hc = new HealthCheck();
        hc.setMessage("db-ok");
        repository.save(hc);
        return "DB OK";
    }
}
