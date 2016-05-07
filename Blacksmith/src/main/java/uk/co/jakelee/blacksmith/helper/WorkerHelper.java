package uk.co.jakelee.blacksmith.helper;

import android.content.Context;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Worker_Resource;

public class WorkerHelper {
    public static int getResourceIDByTool(Context context, long toolID) {
        Item tool = Item.findById(Item.class, toolID);
        int resource = getResourceByTool(tool);
        return context.getResources().getIdentifier(resource == 0 ? "lock" : ("item" + resource), "drawable", context.getPackageName());
    }

    public static int getResourceByTool (Item tool) {
        List<Worker_Resource> resources = getResourcesByTool(tool.getId());
        return resources.size() > 0 ? resources.get(0).getResourceID() : 0;
    }

    public static List<Worker_Resource> getResourcesByTool(long toolID) {
        return getResourcesByTool((int) toolID);
    }
    public static List<Worker_Resource> getResourcesByTool(int toolID) {
        return Select.from(Worker_Resource.class).where(
                Condition.prop("tool_id").eq(toolID)).list();
    }

    public static boolean isValidTool(long toolID) {
        return isValidTool((int) toolID);
    }

    public static boolean isValidTool(int toolID) {
        return Select.from(Worker_Resource.class).where(
                Condition.prop("tool_id").eq(toolID)).count() > 0;
    }
}
