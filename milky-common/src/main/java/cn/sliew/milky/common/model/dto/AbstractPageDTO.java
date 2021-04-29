package cn.sliew.milky.common.model.dto;

import java.util.Collection;

public abstract class AbstractPageDTO<T> extends AbstractResultDTO {

    /**
     * total records num.
     */
    private long total;

    /**
     * page from param.
     */
    private long page;

    /**
     * size from param.
     */
    private long size;

    /**
     * max available page.
     */
    private long max;

    /**
     * records detail.
     */
    private Collection<T> details;

}
