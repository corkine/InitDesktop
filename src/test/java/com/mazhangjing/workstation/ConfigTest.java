package com.mazhangjing.workstation;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test public void readYAML() {
        Yaml yaml = new Yaml();
        Config config = yaml.loadAs(getClass().getClassLoader().getResourceAsStream("config.yaml"), Config.class);
        System.out.println(config);
        List<Map<String, Object>> maps = config.getAppList().stream().map(
                map -> {
                    Map.Entry<String, Object> entry = map.entrySet().iterator().next();
                    Map<String, Object> bean = Optional.ofNullable((Map<String, Object>) entry.getValue()).orElse(new HashMap<>());
                    return bean;
                }
        ).collect(Collectors.toList());
        System.out.println("maps = " + maps);
    }

}