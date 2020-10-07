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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public Collection<T> getDetails() {
        return details;
    }

    public void setDetails(Collection<T> details) {
        this.details = details;
    }
}
