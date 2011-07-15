package it.tika;

import org.restlet.resource.ServerResource;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-14
 * Time: 下午4:58
 * To change this template use File | Settings | File Templates.
 */
public interface OnRequestListener {
    public void onRequest(ServerResource resource);
}
