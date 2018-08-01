package me.jinky.fwk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class BukkitCompleter
  implements TabCompleter
{
  @SuppressWarnings({ "unchecked", "rawtypes" })
private Map<String, Map.Entry<Method, Object>> completers = new HashMap();
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
public void addCompleter(String label, Method m, Object obj)
  {
    this.completers.put(label, new AbstractMap.SimpleEntry(m, obj));
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    for (int i = args.length; i >= 0; i--)
    {
      StringBuffer buffer = new StringBuffer();
      buffer.append(label.toLowerCase());
      for (int x = 0; x < i; x++) {
        if ((!args[x].equals("")) && (!args[x].equals(" "))) {
          buffer.append("." + args[x].toLowerCase());
        }
      }
      String cmdLabel = buffer.toString();
      if (this.completers.containsKey(cmdLabel))
      {
        Map.Entry<Method, Object> entry = (Map.Entry)this.completers.get(cmdLabel);
        try
        {
          return (List)((Method)entry.getKey()).invoke(entry.getValue(), new Object[] {
            new CommandArgs(sender, command, label, args, cmdLabel.split("\\.").length - 1) });
        }
        catch (IllegalArgumentException e)
        {
          e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
          e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
          e.printStackTrace();
        }
      }
    }
    return null;
  }
}
