package data.io.exporter;

import java.util.List;

public record MapExporterData(
        List<String> names,
        float[] bounds,
        int tileSize
) implements ExporterData {  }
