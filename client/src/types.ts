export interface ChartQuery {
    begin_date: string;
    end_date: string;
    countries: number[];
}

export interface SelectableOptions {
    vaccinations: boolean;
    newCases: boolean; //DEVTEMP
    deaths: boolean;
}
