package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.widget.LinearLayout;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Worker;
import uk.co.jakelee.blacksmith.model.Worker_Resource;

public class WorkerHelper {
    public static List<Integer> getResourceIDsByTool(Context context, long toolID) {
        List<Integer> resourceIDs = new ArrayList<>();
        List<Worker_Resource> resources = getResourcesByTool(toolID);

        for (Worker_Resource resource : resources) {

        }
        return resourceIDs;
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

    public static void populateResources(DisplayHelper dh, LinearLayout container, long toolID) {
        List<Worker_Resource> resources = getResourcesByTool(toolID);
        for (Worker_Resource resource : resources) {
            container.addView(dh.createImageView("item", String.valueOf(resource.getResourceID()), 22, 22));
        }
    }

    public static boolean isReady(Worker worker) {
        return worker.getTimeStarted() == 0;
    }

    public static String getTimeRemainingString(Worker worker) {
        long timeStarted = worker.getTimeStarted();
        long timeForCompletion = DateHelper.minutesToMilliseconds(Constants.WORKER_MINUTES);
        long difference = System.currentTimeMillis() - (timeStarted + timeForCompletion);

        return DateHelper.getHoursMinsRemaining(difference);
    }

    public static int getBuyCost(Worker worker) {
        return worker.getLevelUnlocked() * Constants.WORKER_COST_MULTIPLIER;
    }

    public static String getButtonText(Worker worker) {
        if (isReady(worker)) {
            return "Send Out Gathering";
        } else {
            return "Returns In " + WorkerHelper.getTimeRemainingString(worker);
        }
    }
}
