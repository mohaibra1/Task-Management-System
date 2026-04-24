package taskmanagement.dto;

public class TaskListDTO extends TaskDTO{
    private long total_comments;

    public long getTotal_comments() { return total_comments; }
    public void setTotal_comments(long total_comments) { this.total_comments = total_comments; }
}
