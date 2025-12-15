package com.example.demo.dto;

import java.util.List;

public class StatisticsResponse {
    private Long totalRedeemed;
    private Long totalGiftTypes;
    private List<DailyTrend> dailyTrends;
    private List<WeeklyTrend> weeklyTrends;
    private List<MonthlyTrend> monthlyTrends;
    
    public Long getTotalRedeemed() {
        return totalRedeemed;
    }

    public void setTotalRedeemed(Long totalRedeemed) {
        this.totalRedeemed = totalRedeemed;
    }

    public Long getTotalGiftTypes() {
        return totalGiftTypes;
    }

    public void setTotalGiftTypes(Long totalGiftTypes) {
        this.totalGiftTypes = totalGiftTypes;
    }

    public List<DailyTrend> getDailyTrends() {
        return dailyTrends;
    }

    public void setDailyTrends(List<DailyTrend> dailyTrends) {
        this.dailyTrends = dailyTrends;
    }

    public List<WeeklyTrend> getWeeklyTrends() {
        return weeklyTrends;
    }

    public void setWeeklyTrends(List<WeeklyTrend> weeklyTrends) {
        this.weeklyTrends = weeklyTrends;
    }

    public List<MonthlyTrend> getMonthlyTrends() {
        return monthlyTrends;
    }

    public void setMonthlyTrends(List<MonthlyTrend> monthlyTrends) {
        this.monthlyTrends = monthlyTrends;
    }

    public static class DailyTrend {
        private String date;
        private Long count;
        
        public DailyTrend(String date, Long count) {
            this.date = date;
            this.count = count;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
    
    public static class WeeklyTrend {
        private String week;
        private Long count;
        
        public WeeklyTrend(String week, Long count) {
            this.week = week;
            this.count = count;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
    
    public static class MonthlyTrend {
        private String month;
        private Long count;
        
        public MonthlyTrend(String month, Long count) {
            this.month = month;
            this.count = count;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}