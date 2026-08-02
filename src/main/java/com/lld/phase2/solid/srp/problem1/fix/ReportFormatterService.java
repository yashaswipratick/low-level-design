package com.lld.phase2.solid.srp.problem1.fix;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// TODO:
//   2. HTML layout / format changes     → Fixed

@Component
public class ReportFormatterService {

    public String formatAsHtml(List<Map<String, Object>> data) {
        StringBuilder html = new StringBuilder("<table border='1'>");
        html.append("<tr><th>Column</th><th>Value</th></tr>");
        for (Map<String, Object> row : data) {
            html.append("<tr>");
            row.forEach((k, v) -> html.append("<td>").append(k).append("</td>")
                                      .append("<td>").append(v).append("</td>"));
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }
}
