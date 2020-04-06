package com.erokhina.report.http;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import com.erokhina.common.db.ConnectionProvider;
import com.erokhina.common.query.QueryProcessor;
import com.erokhina.report.query.queries.GetStatsQuery;
import com.erokhina.report.query.QueryDaoImpl;
import com.github.vanbv.num.AbstractHttpMappingHandler;
import com.github.vanbv.num.annotation.Get;
import com.github.vanbv.num.annotation.QueryParam;
import com.github.vanbv.num.json.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@ChannelHandler.Sharable
public class HttpMappingHandler extends AbstractHttpMappingHandler {
    private QueryProcessor queryProcessor;

    public HttpMappingHandler(JsonParser parser) throws Exception {
        super(parser);
        this.queryProcessor = new QueryProcessor(new QueryDaoImpl(ConnectionProvider.connect()));
    }

    DefaultFullHttpResponse wrapResult(String result) {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.copiedBuffer(result, StandardCharsets.UTF_8));
    }

    @Get("/stats")
    public DefaultFullHttpResponse enter(@QueryParam(value = "from") String fromDate,
                                         @QueryParam(value = "to") String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        return wrapResult(queryProcessor.process(new GetStatsQuery(from, to)));
    }
}
