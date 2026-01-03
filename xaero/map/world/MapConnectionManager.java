//Decompiled by Procyon!

package xaero.map.world;

import java.io.*;
import java.util.*;

public class MapConnectionManager
{
    private Map<MapConnectionNode, Set<MapConnectionNode>> allConnections;
    
    public MapConnectionManager() {
        this.allConnections = new HashMap<MapConnectionNode, Set<MapConnectionNode>>();
    }
    
    public void addConnection(final MapConnectionNode mapKey1, final MapConnectionNode mapKey2) {
        this.addOneWayConnection(mapKey1, mapKey2);
        this.addOneWayConnection(mapKey2, mapKey1);
    }
    
    private void addOneWayConnection(final MapConnectionNode mapKey1, final MapConnectionNode mapKey2) {
        Set<MapConnectionNode> connections = this.allConnections.get(mapKey1);
        if (connections == null) {
            this.allConnections.put(mapKey1, connections = new HashSet<MapConnectionNode>());
        }
        connections.add(mapKey2);
    }
    
    public void removeConnection(final MapConnectionNode mapKey1, final MapConnectionNode mapKey2) {
        this.removeOneWayConnection(mapKey1, mapKey2);
        this.removeOneWayConnection(mapKey2, mapKey1);
    }
    
    private void removeOneWayConnection(final MapConnectionNode mapKey1, final MapConnectionNode mapKey2) {
        final Set<MapConnectionNode> connections = this.allConnections.get(mapKey1);
        if (connections == null) {
            return;
        }
        connections.remove(mapKey2);
    }
    
    public boolean isConnected(final MapConnectionNode mapKey1, final MapConnectionNode mapKey2) {
        if (mapKey1 == null || mapKey2 == null) {
            return false;
        }
        if (mapKey1.equals(mapKey2)) {
            return true;
        }
        final Set<MapConnectionNode> connections = this.allConnections.get(mapKey1);
        return connections != null && connections.contains(mapKey2);
    }
    
    public boolean isEmpty() {
        return this.allConnections.isEmpty();
    }
    
    public void save(final PrintWriter writer) {
        if (!this.allConnections.isEmpty()) {
            final Set<String> redundantConnections = new HashSet<String>();
            for (final Map.Entry<MapConnectionNode, Set<MapConnectionNode>> entry : this.allConnections.entrySet()) {
                final MapConnectionNode mapKey = entry.getKey();
                final Set<MapConnectionNode> connections = entry.getValue();
                for (final MapConnectionNode c : connections) {
                    final String fullConnection = mapKey + ":" + c;
                    if (redundantConnections.contains(fullConnection)) {
                        continue;
                    }
                    writer.println("connection:" + fullConnection);
                    redundantConnections.add(c + ":" + mapKey);
                }
            }
        }
    }
    
    private void swapConnections(final MapConnectionNode mapKey1, final MapConnectionNode mapKey2) {
        final Set<MapConnectionNode> connections1 = new HashSet<MapConnectionNode>(this.allConnections.getOrDefault(mapKey1, new HashSet<MapConnectionNode>()));
        final Set<MapConnectionNode> connections2 = new HashSet<MapConnectionNode>(this.allConnections.getOrDefault(mapKey2, new HashSet<MapConnectionNode>()));
        for (final MapConnectionNode c : connections1) {
            this.removeConnection(mapKey1, c);
        }
        for (final MapConnectionNode c : connections2) {
            this.addConnection(mapKey1, c);
        }
        for (final MapConnectionNode c : connections2) {
            this.removeConnection(mapKey2, c);
        }
        for (final MapConnectionNode c : connections1) {
            this.addConnection(mapKey2, c);
        }
    }
}
