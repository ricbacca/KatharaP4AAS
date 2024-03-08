// Copyright 2023 riccardo.bacca@studio.unibo.it
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package p4_aas.RyuController.Models;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.compress.utils.Lists;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AllFlowStats {
    int priority;
    int byte_count;
    int duration_sec;
    int packet_count;
    int length;
    int flags;
    List<String> actions = Lists.newArrayList();
    List<Match> matchList = Lists.newArrayList();
    int table_id;

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "AllFlowStats [priority=" + priority + ", byte_count=" + byte_count + ", duration_sec=" + duration_sec
                + ", packet_count=" + packet_count + ", length=" + length + ", flags=" + flags + ", actions=" + actions
                + ", list=" + matchList + ", table_id=" + table_id + "]";
    }

    public SubmodelElement[] formatResult() {
        return new SubmodelElement[]{
            createProperty("Priority", "Priority: " + this.priority),
            createProperty("ByteCount", "Byte Count: " + this.byte_count),
            createProperty("Duration", "Duration Sec: " + this.duration_sec),
            createProperty("PacketCount", "Packet count: " + this.packet_count),
            createProperty("Length", "Length: " + this.length),
            createProperty("Flags", "Flags: " + this.flags),
            createProperty("Actions", "Actions: " + this.actions),
            createProperty("MatchList", "Match list: " + this.matchList),
            createProperty("TableId", "TableId: " + this.table_id),
        };
    }

    static private SubmodelElement[] createResults(List<Object> priority, List<Object> byte_count, List<Object> duration_sec, List<Object> packet_count, List<Object> length, List<Object> flags, List<Object> actions, List<Object> matchList, List<Object> table_id) {
        return new SubmodelElement[]{
            createProperty("Priority", "Priority: " + priority),
            createProperty("ByteCount", "Byte Count: " + byte_count),
            createProperty("Duration", "Duration Sec: " + duration_sec),
            createProperty("PacketCount", "Packet count: " + packet_count),
            createProperty("Length", "Length: " + length),
            createProperty("Flags", "Flags: " + flags),
            createProperty("Actions", "Actions: " + actions),
            createProperty("MatchList", "Match list: " + matchList),
            createProperty("TableId", "TableId: " + table_id),
        };
    }

    static public SubmodelElement[] formatMultipleResults(List<AllFlowStats[]> list) {
        List<AllFlowStats> finalList = new LinkedList<>();
        list.forEach(el -> {
            for (int i=0; i<el.length; i++) {
                finalList.add(el[i]);
            }
        });

        Map<String, List<Object>> finalResult = new HashMap<String, List<Object>>();

        for(Field f: AllFlowStats.class.getDeclaredFields()) {
            List<Object> tempList = new LinkedList<>();
            for(AllFlowStats el: finalList) {
                try {
                    tempList.add(f.get(el));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            finalResult.put(f.getName(), List.copyOf(tempList));
            tempList.clear();
        }
        
        return createResults(
            finalResult.get("priority"), 
            finalResult.get("byte_count"), 
            finalResult.get("duration_sec"), 
            finalResult.get("packet_count"), 
            finalResult.get("length"),
            finalResult.get("flags"), 
            finalResult.get("actions"), 
            finalResult.get("matchList"), 
            finalResult.get("table_id"));
    }

    static private SubmodelElement createProperty(String idShort, Object value) {
        SubmodelElement el = new Property(idShort, value);
        el.setKind(ModelingKind.TEMPLATE);
        return el;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getByte_count() {
        return byte_count;
    }

    public void setByte_count(int byte_count) {
        this.byte_count = byte_count;
    }

    public int getDuration_sec() {
        return duration_sec;
    }

    public void setDuration_sec(int duration_sec) {
        this.duration_sec = duration_sec;
    }

    public int getPacket_count() {
        return packet_count;
    }

    public void setPacket_count(int packet_count) {
        this.packet_count = packet_count;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<Match> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<Match> list) {
        this.matchList = list;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    static class Match {
        String dl_dst;
        int dl_type;

        public String getDl_dst() {
            return dl_dst;
        }
        public void setDl_dst(String dl_dst) {
            this.dl_dst = dl_dst;
        }
        public int getDl_type() {
            return dl_type;
        }
        public void setDl_type(int dl_type) {
            this.dl_type = dl_type;
        }
    }
}