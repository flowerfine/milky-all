package cn.sliew.milky.common.filter;

public class DelegatingActionListener<Response> implements ActionListener<Response> {

    private final ActionListener<Response> delegate;

    public DelegatingActionListener(ActionListener<Response> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onResponse(Response response) {
        delegate.onResponse(response);
    }

    @Override
    public void onFailure(Throwable throwable) {
        delegate.onFailure(throwable);
    }
}