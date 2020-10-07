package cn.sliew.milky.common.model.param;

public abstract class AbstractPageParam extends AbstractParam {

    public static final long MAX_PAGE = 20;
    public static final long MAX_SIZE = 100;

    public static final long DEFAULT_PAGE = 1L;
    public static final long DEFAULT_SIZE = 20L;

    /**
     * 页码, 默认为1.
     * 最小为1, 最大为20
     */
    private long page = DEFAULT_PAGE;

    /**
     * 分页, 默认为20.
     * 最小为1, 最大为100
     */
    private long size = DEFAULT_SIZE;

    public long getPage() {
        return page > 0 ? (page <= MAX_PAGE ? page : MAX_PAGE) : DEFAULT_PAGE;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size > 0 ? (size <= MAX_SIZE ? size : MAX_SIZE) : DEFAULT_SIZE;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
