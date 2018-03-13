package uk.rgu.csdm.ubs.count;

public interface Counter {
    void add(Double[][] frame);
    int getCount();
    void clear();
}
