import { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';

interface AdminChartProps {
  title: string;
  data: any[];
  labelKey: string;  
  valueKey: string;  
  color: string;      
  canvasId: string;
}

export default function AdminChart({ title, data, labelKey, valueKey, color, canvasId }: AdminChartProps) {
  const chartRef = useRef<Chart | null>(null);

  const fillPlaceholderData = (rawData: any[]) => {
    const minItems = 10;
    const filledData = [...rawData];
    for (let i = filledData.length; i < minItems; i++) {
      filledData.push({
        [labelKey]: "---",
        [valueKey]: 0
      });
    }
    return filledData;
  };

  useEffect(() => {
    const ctx = document.getElementById(canvasId) as HTMLCanvasElement;
    
    if (ctx && data) {
      if (chartRef.current) {
        chartRef.current.destroy();
      }

      const processedData = fillPlaceholderData(data);

      chartRef.current = new Chart(ctx, {
        type: "bar",
        data: {
          labels: processedData.map(d => d[labelKey]),
          datasets: [{
            label: title,
            data: processedData.map(d => d[valueKey]),
            backgroundColor: color + "73", 
            borderColor: color,
            borderWidth: 1,
            borderRadius: 6
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false }
          },
          scales: {
            y: { 
              beginAtZero: true, 
              suggestedMax: 10, 
              ticks: { precision: 0 } 
            },
            x: { 
              ticks: { maxRotation: 45, minRotation: 0 } 
            }
          }
        }
      });
    }

    return () => {
      if (chartRef.current) {
        chartRef.current.destroy();
      }
    };
  }, [data, canvasId, color, title, labelKey, valueKey]);

  return (
    <div style={{ height: '300px', position: 'relative' }}>
      <canvas id={canvasId}></canvas>
    </div>
  );
}