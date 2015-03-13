package org.baracus.orm;

/**
 * Created by marcus on 03.07.14.
 */
public interface OptmisticLocking {
    int getVersion();

    void setVersion(int newVersion);
}
