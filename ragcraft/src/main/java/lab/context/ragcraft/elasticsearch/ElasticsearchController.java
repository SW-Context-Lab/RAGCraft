package lab.context.ragcraft.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ElasticsearchController {
    private final ElasticsearchClient client;

    @GetMapping("info/es")
    public Map<String, Object> info() throws IOException {
        InfoResponse info = client.info();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("clusterName", info.clusterName());
        res.put("clusterUuid", info.clusterUuid());
        res.put("nodeName", info.name());
        res.put("tagline", info.tagline());

        if (info.version() != null) {
            res.put("versionNumber", info.version().number());
            res.put("buildFlavor", info.version().buildFlavor());
            res.put("buildType", info.version().buildType());
            res.put("luceneVersion", info.version().luceneVersion());
        }

        return res;
    }

    @GetMapping("/health/es")
    public Map<String, Object> health() throws IOException {
        HealthResponse h = client.cluster().health();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("clusterName", h.clusterName());
        res.put("status", h.status() != null ? h.status().jsonValue() : null);
        res.put("numberOfNodes", h.numberOfNodes());
        res.put("numberOfDataNodes", h.numberOfDataNodes());
        res.put("activePrimaryShards", h.activePrimaryShards());
        res.put("activeShards", h.activeShards());
        res.put("unassignedShards", h.unassignedShards());
        res.put("relocatingShards", h.relocatingShards());
        res.put("initializingShards", h.initializingShards());

        return res;
    }
}
