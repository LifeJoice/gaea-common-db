package org.gaea.data.dataset.convertor;

import org.apache.commons.lang3.NotImplementedException;
import org.gaea.data.dataset.domain.XmlApiPage;
import org.gaea.data.dataset.domain.XmlDsParam;
import org.gaea.exception.InvalidDataException;
import org.gaea.util.GaeaXmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

/**
 * 负责{@code <api-data-source> -> <param>}的转换
 * Created by iverson on 2017年12月1日 星期五
 */
@Component
public class XmlDsParamsConverter {
    private final Logger logger = LoggerFactory.getLogger(XmlDsParamsConverter.class);

    /**
     * 负责转换单个{@code <param>}元素。
     *
     * @param node
     * @return
     * @throws InvalidDataException
     */
    public XmlDsParam convertParam(Node node) throws InvalidDataException {
        // 解析<paging>
        XmlDsParam dsParam = new XmlDsParam();

        try {
            dsParam = GaeaXmlUtils.copyAttributesToBean(node, dsParam, XmlDsParam.class);
        } catch (Exception e) {
            String errorMsg = "自动转换XML元素<paging>的属性错误！";
            throw new InvalidDataException(errorMsg, e);
        }
        return dsParam;
    }
}
