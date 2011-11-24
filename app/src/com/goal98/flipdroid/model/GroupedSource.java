package com.goal98.flipdroid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * User: ITS
 * Date: 11-7-7
 * Time: 上午9:38
 * To change this template use File | Settings | File Templates.
 */
public class GroupedSource {
    private List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
    List<List<Map<String, String>>> children = new ArrayList<List<Map<String, String>>>();

    public List<List<Map<String, String>>> getChildren() {
        return children;
    }

    public void setChildren(List<List<Map<String, String>>> children) {
        this.children = children;
    }

    public List<Map<String, String>> getGroups() {
        return groups;
    }

    public void setGroups(List<Map<String, String>> groups) {
        this.groups = groups;
    }

    public void addGroup(String keyName, String groupName) {
        Map<String, String> group = new HashMap<String, String>();
        group.put(RSSSourceRepo.KEY_NAME_GROUP, groupName);
        groups.add(group);
    }

    public void addChild(String groupName, Map<String, String> customeSection) {
        for (int i = 0; i < groups.size(); i++) {
            Map<String, String> group = groups.get(i);
            if (group.values().contains(groupName)) {
                List<Map<String, String>> child = new ArrayList<Map<String, String>>();
                child.add(customeSection);
                children.add(child);
                break;
            }

        }
    }
}
